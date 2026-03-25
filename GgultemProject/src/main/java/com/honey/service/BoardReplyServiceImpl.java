package com.honey.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.Board;
import com.honey.domain.BoardReply;
import com.honey.domain.Member;
import com.honey.dto.BoardReplyDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BoardReplyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardReplyServiceImpl implements BoardReplyService {

    private final BoardReplyRepository boardReplyRepository;

    ///////////////////
    /// 댓글 등록
    ///////////////////
    @Override
    public Long register(BoardReplyDTO dto) {

        Board board = Board.builder()
                .boardNo(dto.getBoardNo())
                .build();

        Member member = Member.builder()
                .email(dto.getEmail())
                .build();

        BoardReply reply = BoardReply.builder()
                .board(board)
                .member(member)
                .content(dto.getContent())
                .enabled(1)
                .build();

        return boardReplyRepository.save(reply).getReplyNo();
    }

    ///////////////////
    /// 댓글 목록
    ///////////////////
    @Override
    public List<BoardReplyDTO> list(Integer boardNo) {

        return boardReplyRepository.findByBoardBoardNo(boardNo).stream()
                .map(reply -> BoardReplyDTO.builder()
                        .replyNo(reply.getReplyNo())
                        .boardNo(reply.getBoard().getBoardNo())
                        .email(reply.getMember().getEmail())
                        .content(reply.getContent())
                        .enabled(reply.getEnabled())
                        .build())
                .toList();
    }

    ///////////////////
    /// 댓글 수정
    ///////////////////
    @Override
    public void modify(BoardReplyDTO dto) {

        BoardReply reply = boardReplyRepository.findById(dto.getReplyNo())
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        // Entity 방식대로 수정
        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            reply.changeContent(dto.getContent());
        }
    }

    ///////////////////
    /// 댓글 삭제 (일반)
    ///////////////////
    @Override
    public void remove(Long replyNo) {

        BoardReply reply = boardReplyRepository.findById(replyNo)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        reply.changeEnabled(0);
    }

    ///////////////////
    /// 관리자 댓글 목록 (핵심)
    ///////////////////
    @Override
    public PageResponseDTO<BoardReplyDTO> adminReplyList(SearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getSize(),
                Sort.by("replyNo").descending()
        );

        String keyword = searchDTO.getKeyword();
        Integer enabled = Integer.parseInt(searchDTO.getEnabled());

        // 🔥 빈값 방어 (이거 중요)
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        // 🔥 이제 이거 한줄로 끝
        Page<BoardReply> result =
                boardReplyRepository.searchAll(enabled, keyword, pageable);

        List<BoardReplyDTO> dtoList = result.getContent().stream()
                .map(reply -> BoardReplyDTO.builder()
                        .replyNo(reply.getReplyNo())
                        .boardNo(reply.getBoard().getBoardNo())
                        .email(reply.getMember().getEmail())
                        .content(reply.getContent())
                        .enabled(reply.getEnabled())
                        .build())
                .toList();

        return PageResponseDTO.<BoardReplyDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    ///////////////////
    /// 관리자 삭제
    ///////////////////
    @Override
    public void adminRemove(Long replyNo) {

        BoardReply reply = boardReplyRepository.findById(replyNo)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        reply.changeEnabled(0);
    }
}