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
    /// 게시글 등록
    ///////////////////
    @Override
    public Integer register(BoardDTO boardDTO) {

        Member member = memberRepository.findById(boardDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Board board = Board.builder()
                .title(boardDTO.getTitle())
                .writer(member.getNickname())
                .content(boardDTO.getContent())
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

        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        board.changeViewCount(board.getViewCount() + 1);

        BoardDTO dto = modelMapper.map(board, BoardDTO.class);

        List<String> fileNames = board.getBoardImage()
                .stream()
                .map(img -> img.getFileName())
                .toList();

        dto.setUploadFileNames(fileNames);

        return dto;
    }

    ///////////////////
    /// 게시글 수정
    ///////////////////
    @Override
    public void modify(BoardDTO boardDTO) {

        Board board = boardRepository.findById(boardDTO.getBoardNo())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if (boardDTO.getTitle() != null && !boardDTO.getTitle().isEmpty()) {
            board.changeTitle(boardDTO.getTitle());
        }

        if (boardDTO.getContent() != null && !boardDTO.getContent().isEmpty()) {
            board.setContent(boardDTO.getContent());
        }
    }

    ///////////////////
    /// 게시글 삭제 (논리삭제)
    ///////////////////
    @Override
    public void remove(Integer boardNo) {

        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        board.changeEnabled(0);
    }

    ///////////////////
    /// 게시글 목록 (일반 사용자)
    ///////////////////
    @Override
    public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getSize(),
                Sort.by("boardNo").descending()
        );

        Page<Board> result;

        if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

            result = boardRepository.searchByCondition(
                    searchDTO.getSearchType(),
                    searchDTO.getKeyword(),
                    pageable
            );

        } else {
            result = boardRepository.findAllActive(pageable);
        }

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> {

                    BoardDTO dto = modelMapper.map(board, BoardDTO.class);

                    List<String> fileNames = board.getBoardImage()
                            .stream()
                            .map(img -> img.getFileName())
                            .toList();

                    dto.setUploadFileNames(fileNames);

                    return dto;
                })
                .collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    ///////////////////
    /// 관리자 게시글 목록 (🔥 수정 완료)
    ///////////////////
    @Override
    public PageResponseDTO<BoardDTO> adminList(SearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getSize(),
                Sort.by("boardNo").descending()
        );

        // 🔥 keyword 처리
        String keyword = searchDTO.getKeyword();
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 🔥 enabled null 방어 (핵심)
        Integer enabled = null;
        if (searchDTO.getEnabled() != null && !searchDTO.getEnabled().isEmpty()) {
            enabled = Integer.parseInt(searchDTO.getEnabled());
        }

        // 🔥 관리자 검색 실행
        Page<Board> result =
                boardRepository.searchAllAdmin(enabled, keyword, pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> {

                    BoardDTO dto = modelMapper.map(board, BoardDTO.class);

                    List<String> fileNames = board.getBoardImage()
                            .stream()
                            .map(img -> img.getFileName())
                            .toList();

                    dto.setUploadFileNames(fileNames);

                    return dto;
                })
                .toList();

        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    ///////////////////
    /// 관리자 게시글 삭제
    ///////////////////
    @Override
    public void adminRemove(Integer boardNo) {

        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        board.changeEnabled(0);
    }
}