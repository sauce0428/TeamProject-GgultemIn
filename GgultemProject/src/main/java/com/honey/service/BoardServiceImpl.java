package com.honey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.Board;
import com.honey.domain.Member;
import com.honey.dto.BoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BoardRepository;
import com.honey.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

	private final ModelMapper modelMapper;
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;

	///////////////////
	/// HTML 제거 코드
	///////////////////
	private String extractText(String html) {
		if (html == null)
			return null;

		return html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
	}

	///////////////////
	/// 게시글 등록
	///////////////////
	@Override
	public Integer register(BoardDTO boardDTO) {

		Member member = memberRepository.findById(boardDTO.getEmail()).orElseThrow(() -> new RuntimeException("회원 없음"));

		String text = extractText(boardDTO.getContent());

		Board board = Board.builder()
				.title(boardDTO.getTitle())
				.writer(member.getNickname())
				.content(boardDTO.getContent())
				.contentText(text)
				.viewCount(0)
				.enabled(1)
				.member(member)
				.build();

		return boardRepository.save(board).getBoardNo();
	}

	///////////////////
	/// 게시글 조회
	///////////////////
	@Override
	public BoardDTO get(Integer boardNo) {

		Board board = boardRepository.findById(boardNo).orElseThrow(() -> new RuntimeException("게시글 없음"));

		board.changeViewCount(board.getViewCount() + 1);

		BoardDTO dto = modelMapper.map(board, BoardDTO.class);

		List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

		dto.setUploadFileNames(fileNames);

		return dto;
	}

	///////////////////
	/// 게시글 수정
	///////////////////
	@Override
	public void modify(BoardDTO boardDTO) {

		Board board = boardRepository.findById(boardDTO.getBoardNo()).orElseThrow(() -> new RuntimeException("게시글 없음"));

		if (boardDTO.getTitle() != null && !boardDTO.getTitle().isEmpty()) {
			board.changeTitle(boardDTO.getTitle());
		}

		if (boardDTO.getContent() != null && !boardDTO.getContent().isEmpty()) {

			board.setContent(boardDTO.getContent());

			String text = extractText(boardDTO.getContent());
			board.changeContentText(text);
		}
	}

	///////////////////
	/// 게시글 삭제 (논리삭제)
	///////////////////
	@Override
	public void remove(Integer boardNo) {

		Board board = boardRepository.findById(boardNo).orElseThrow(() -> new RuntimeException("게시글 없음"));

		board.changeEnabled(0);
	}

///////////////////
//게시글 목록 (일반 사용자)
///////////////////
	@Override
	public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {

		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize());

		Page<Object[]> result;

		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			result = boardRepository.searchByCondition(searchDTO.getSearchType(), searchDTO.getKeyword(), pageable);

		} else {
			result = boardRepository.findAllActive(pageable);
		}

		List<BoardDTO> dtoList = result.getContent().stream().map(arr -> {

			Board board = (Board) arr[0];
			Long replyCount = (Long) arr[1];

			BoardDTO dto = modelMapper.map(board, BoardDTO.class);

			dto.setReplyCount(replyCount.intValue()); // 댓글수

			List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

			dto.setUploadFileNames(fileNames);

			return dto;
		}).toList();

		return PageResponseDTO.<BoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}

///////////////////
//관리자 게시글 목록
///////////////////
	@Override
	public PageResponseDTO<BoardDTO> adminList(SearchDTO searchDTO) {

		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("boardNo").descending());

		String keyword = searchDTO.getKeyword();
		if (keyword != null && keyword.trim().isEmpty()) {
			keyword = null;
		}

		Integer enabled = null;
		if (searchDTO.getEnabled() != null && !searchDTO.getEnabled().isEmpty()) {
			enabled = Integer.parseInt(searchDTO.getEnabled());
		}

		Page<Object[]> result = boardRepository.searchAllAdmin(enabled, keyword, pageable);

		List<BoardDTO> dtoList = result.getContent().stream().map(arr -> {

			Board board = (Board) arr[0];
			Long replyCount = (Long) arr[1];

			BoardDTO dto = modelMapper.map(board, BoardDTO.class);

			dto.setReplyCount(replyCount.intValue()); // 댓글수

			List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

			dto.setUploadFileNames(fileNames);

			return dto;
		}).toList();

		return PageResponseDTO.<BoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}

	///////////////////
	/// 관리자 게시글 삭제
	///////////////////
	@Override
	public void adminRemove(Integer boardNo) {

		Board board = boardRepository.findById(boardNo).orElseThrow(() -> new RuntimeException("게시글 없음"));

		board.changeEnabled(0);
	}
}