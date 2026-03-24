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
import com.honey.domain.BoardReply;
import com.honey.domain.Member;
import com.honey.dto.BoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BoardReplyRepository;
import com.honey.repository.BoardRepository;
import com.honey.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional // 서비스 전체 트랜잭션 처리
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final ModelMapper modelMapper;
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;
	private final BoardReplyRepository boardReplyRepository;

	// =========================
	// 게시글 등록
	// =========================
	@Override
	public Integer register(BoardDTO boardDTO) {

		// 이메일로 회원 조회 (작성자 정보)
		Member member = memberRepository.findById(boardDTO.getEmail()).orElseThrow();

		// 게시글 엔티티 생성
		Board board = Board.builder().title(boardDTO.getTitle()).writer(member.getNickname())
				.content(boardDTO.getContent()).viewCount(0) // 조회수 초기값
				.enabled(1) // 활성 상태 (1: 활성, 0: 삭제)
				.member(member).build();

		// DB 저장 후 PK 반환
		return boardRepository.save(board).getBoardNo();
	}

	// =========================
	// 게시글 조회
	// =========================
	@Override
	public BoardDTO get(Integer boardNo) {

		// 게시글 조회
		Board board = boardRepository.findById(boardNo).orElseThrow();

		// 조회수 증가
		board.changeViewCount(board.getViewCount() + 1);

		// Entity → DTO 변환
		BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

		// 이미지 파일명 리스트 추출
		List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

		boardDTO.setUploadFileNames(fileNames);

		return boardDTO;
	}

	// =========================
	// 게시글 수정
	// =========================
	@Override
	public void modify(BoardDTO boardDTO) {

		// 수정할 게시글 조회
		Board board = boardRepository.findById(boardDTO.getBoardNo()).orElseThrow();

		// 제목 수정 (null 방어)
		if (boardDTO.getTitle() != null && !boardDTO.getTitle().isEmpty()) {
			board.changeTitle(boardDTO.getTitle());
		}

		// 내용 수정
		if (boardDTO.getContent() != null && !boardDTO.getContent().isEmpty()) {
			board.setContent(boardDTO.getContent());
		}

		// 변경된 엔티티 저장
		boardRepository.save(board);
	}

	// =========================
	// 게시글 삭제 (논리삭제)
	// =========================
	@Override
	public void remove(Integer boardNo) {

		// 게시글 조회
		Board board = boardRepository.findById(boardNo).orElseThrow();

		// enabled = 0 → 삭제 처리 (DB에서 실제 삭제 X)
		board.changeEnabled(0);

		boardRepository.save(board);
	}

	// =========================
	// 게시글 목록 (일반 사용자)
	// =========================
	@Override
	public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {

		// 페이징 설정
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("boardNo").descending());

		Page<Board> result;

		// 검색 조건 존재 시
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			result = boardRepository.searchByCondition(searchDTO.getSearchType(), searchDTO.getKeyword(), pageable);

		} else {
			// 일반 사용자 → 활성 게시글만 조회
			result = boardRepository.findAllActive(pageable);
		}

		// Entity → DTO 변환
		List<BoardDTO> dtoList = result.getContent().stream().map(board -> {

			BoardDTO dto = modelMapper.map(board, BoardDTO.class);

			List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

			dto.setUploadFileNames(fileNames);

			return dto;

		}).collect(Collectors.toList());

		// 페이징 DTO 반환
		return PageResponseDTO.<BoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}

	// =========================
	// 관리자 게시글 목록
	// =========================
	@Override
	public PageResponseDTO<BoardDTO> adminList(SearchDTO searchDTO) {

		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("boardNo").descending());

		Page<Board> result;

		// 관리자 검색 (삭제 포함)
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			result = boardRepository.searchByConditionAdmin(searchDTO.getSearchType(), searchDTO.getKeyword(),
					pageable);

		} else {
			// 관리자 → 전체 조회 (삭제 포함)
			result = boardRepository.findAll(pageable);
		}

		List<BoardDTO> dtoList = result.getContent().stream().map(board -> {

			BoardDTO dto = modelMapper.map(board, BoardDTO.class);

			List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

			dto.setUploadFileNames(fileNames);

			return dto;

		}).toList();

		return PageResponseDTO.<BoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}

	// =========================
	// 관리자 게시글 삭제 (권한 무시)
	// =========================
	@Override
	public void adminRemove(Integer boardNo) {

		Board board = boardRepository.findById(boardNo).orElseThrow(() -> new RuntimeException("게시글 없음"));

		// 관리자 → 바로 삭제 처리 (작성자 검증 없음)
		board.changeEnabled(0);
	}

	// =========================
	// 관리자 댓글 삭제
	// =========================
	@Override
	public void removeReply(Long replyNo) {

		BoardReply reply = boardReplyRepository.findById(replyNo).orElseThrow(() -> new RuntimeException("댓글 없음"));

		// 댓글 논리 삭제
		reply.changeEnabled(0);
	}

	@Override
	public PageResponseDTO<BoardReply> adminReplyList(SearchDTO searchDTO) {

		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("replyNo").descending());

		Page<BoardReply> result;

		// 🔥 1. keyword 검색
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().trim().isEmpty()) {

			result = boardReplyRepository.searchReply(searchDTO.getKeyword(), pageable);

			// 🔥 2. 활성/삭제 필터
		} else if (searchDTO.getEnabled() != null && !searchDTO.getEnabled().isEmpty()) {

			result = boardReplyRepository.findByEnabled(Integer.parseInt(searchDTO.getEnabled()), pageable);

			// 🔥 3. 전체 조회
		} else {
			result = boardReplyRepository.findAll(pageable);
		}

		return PageResponseDTO.<BoardReply>withAll().dtoList(result.getContent()).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}
}