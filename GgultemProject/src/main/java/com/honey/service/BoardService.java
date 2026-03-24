package com.honey.service;

import com.honey.dto.BoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface BoardService {

    // =========================
    // 일반 사용자
    // =========================

    // 게시글 등록
    Integer register(BoardDTO boardDTO);

    // 게시글 조회
    BoardDTO get(Integer boardNo);

    // 게시글 수정
    void modify(BoardDTO boardDTO);

    // 게시글 삭제
    void remove(Integer boardNo);

    // 게시글 목록
    PageResponseDTO<BoardDTO> list(SearchDTO searchDTO);

    // =========================
    // 관리자
    // =========================

    // 관지자 전용 게시판목록
    PageResponseDTO<BoardDTO> adminList(SearchDTO searchDTO);

    // 관리자 삭제 (권한 무시)
    void adminRemove(Integer boardNo);
    
    // 관리자 댓글 삭제 
	void removeReply(Long replyNo);
	
	//댓글 리스트
	PageResponseDTO<?> adminReplyList(SearchDTO searchDTO);

}