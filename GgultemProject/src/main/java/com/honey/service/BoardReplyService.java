package com.honey.service;

import java.util.List;

import com.honey.dto.BoardReplyDTO;


public interface BoardReplyService {

    // 댓글 등록
    Long register(BoardReplyDTO boardReplyDTO);

    // 댓글 목록
    List<BoardReplyDTO> list(Integer boardNo);

	// 댓글 수정
	void modify(BoardReplyDTO boardReplyDTO);

    // 댓글 삭제
    void remove(Long no);


}