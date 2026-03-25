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

    // Entity ↔ DTO 변환용
    private final ModelMapper modelMapper;

    // 게시글 Repository
    private final BoardRepository boardRepository;

    // 회원 Repository (작성자 조회용)
    private final MemberRepository memberRepository;

    // =========================
    // 게시글 등록
    // =========================
    @Override
    public Integer register(BoardDTO boardDTO) {

        // 이메일로 회원 조회 (작성자)
        Member member = memberRepository.findById(boardDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 게시글 엔티티 생성
        Board board = Board.builder()
                .title(boardDTO.getTitle())
                .writer(member.getNickname()) // 닉네임 저장
                .content(boardDTO.getContent()) // 에디터 HTML 그대로 저장
                .viewCount(0) // 조회수 초기값
                .enabled(1) // 1: 활성, 0: 삭제
                .member(member)
                .build();

        // 저장 후 PK 반환
        return boardRepository.save(board).getBoardNo();
    }

    // =========================
    // 게시글 조회
    // =========================
    @Override
    public BoardDTO get(Integer boardNo) {

        // 게시글 조회
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        // 조회수 증가
        board.changeViewCount(board.getViewCount() + 1);

        // Entity → DTO 변환
        BoardDTO dto = modelMapper.map(board, BoardDTO.class);

        // 이미지 파일 리스트 추출
        List<String> fileNames = board.getBoardImage()
                .stream()
                .map(img -> img.getFileName())
                .toList();

        dto.setUploadFileNames(fileNames);

        return dto;
    }

    // =========================
    // 게시글 수정
    // =========================
    @Override
    public void modify(BoardDTO boardDTO) {

        // 수정 대상 조회
        Board board = boardRepository.findById(boardDTO.getBoardNo())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        // 제목 수정 (null / 빈값 방어)
        if (boardDTO.getTitle() != null && !boardDTO.getTitle().isEmpty()) {
            board.changeTitle(boardDTO.getTitle());
        }

        // 내용 수정
        if (boardDTO.getContent() != null && !boardDTO.getContent().isEmpty()) {
            board.setContent(boardDTO.getContent());
        }
    }

    // =========================
    // 게시글 삭제 (논리삭제)
    // =========================
    @Override
    public void remove(Integer boardNo) {

        // 게시글 조회
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        // enabled = 0 → 삭제 처리
        board.changeEnabled(0);
    }

    // =========================
    // 게시글 목록 (일반 사용자)
    // =========================
    @Override
    public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {

        // 페이징 설정
        Pageable pageable = PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getSize(),
                Sort.by("boardNo").descending()
        );

        Page<Board> result;

        // 🔥 검색 조건 있을 때
        if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

            result = boardRepository.searchByCondition(
                    searchDTO.getSearchType(),
                    searchDTO.getKeyword(),
                    pageable
            );

        } else {
            // 🔥 일반 사용자 → 삭제 안된 게시글만
            result = boardRepository.findAllActive(pageable);
        }

        // Entity → DTO 변환
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

        // 페이징 DTO 반환
        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    // =========================
    // 관리자 게시글 목록
    // =========================
    @Override
    public PageResponseDTO<BoardDTO> adminList(SearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getSize(),
                Sort.by("boardNo").descending()
        );

        String keyword = searchDTO.getKeyword();
        Integer enabled = Integer.parseInt(searchDTO.getEnabled());

        // 🔥 빈값 방어
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 🔥 통합 검색 (핵심)
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
    // =========================
    // 관리자 게시글 삭제
    // =========================
    @Override
    public void adminRemove(Integer boardNo) {

        // 게시글 조회
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        // 관리자 삭제 → 바로 비활성화
        board.changeEnabled(0);
    }
}