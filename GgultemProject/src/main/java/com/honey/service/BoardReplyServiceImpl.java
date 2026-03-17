//package com.honey.service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.honey.domain.Board;
//import com.honey.domain.BoardReply;
//import com.honey.domain.Member;
//import com.honey.dto.BoardReplyDTO;
//import com.honey.repository.BoardReplyRepository;
//import com.honey.repository.BoardRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class BoardReplyServiceImpl implements BoardReplyService {
//
//    private final BoardReplyRepository boardReplyRepository;
//    private final BoardRepository boardRepository;
//    private final ModelMapper modelMapper;
//
//    // 댓글 등록
//    @Override
//    public Long register(BoardReplyDTO dto) {
//
//        Board board = boardRepository.findById(dto.getBoardNo()).orElseThrow();
//
//        Member member = Member.builder()
//                .no(dto.getMemberNo().longValue())
//                .build();
//        
//        BoardReply parentReply = boardReplyRepository.findById(dto.getParentReplyNo()).orElseThrow();
//        
//        BoardReply reply = BoardReply.builder()
//                .board(board)
//                .member(member)
//                .content(dto.getContent())
//                .parent(parentReply)
//                .enabled(1)
//                .build();
//
//        boardReplyRepository.save(reply);
//
//        return reply.getReplyNo();
//    }
//
//    // 댓글 목록 조회
//    @Override
//    @Transactional(readOnly = true)
//    public List<BoardReplyDTO> list(Integer boardNo) {
//
//        List<BoardReply> list =
//                boardReplyRepository.findByBoardBoardNoAndEnabled(boardNo, 1);
//
//        return list.stream()
//                .map(reply -> {
//
//                    BoardReplyDTO dto = BoardReplyDTO.builder()
//                            .replyNo(reply.getReplyNo())
//                            .boardNo(reply.getBoard().getBoardNo())
//                            .memberNo(reply.getMember().getNo().intValue())
//                            .content(reply.getContent())
//                            .parentReplyNo(
//                                    reply.getParent() != null
//                                            ? reply.getParent().getReplyNo()
//                                            : null
//                            )
//                            .regDate(reply.getRegDate())
//                            .build();
//
//                    return dto;
//                })
//                .collect(Collectors.toList());
//    }
//
//    // 댓글 수정
//    @Override
//    public void modify(BoardReplyDTO dto) {
//
//        BoardReply reply =
//                boardReplyRepository.findById(dto.getReplyNo()).orElseThrow();
//
//        reply.changeContent(dto.getContent());
//    }
//
//    // 댓글 삭제 (논리 삭제)
//    @Override
//    public void remove(Long replyNo) {
//
//        BoardReply reply =
//                boardReplyRepository.findById(replyNo).orElseThrow();
//
//        reply.changeEnabled(0);
//    }
//    
//}