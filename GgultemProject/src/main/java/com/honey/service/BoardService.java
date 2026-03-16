package com.honey.service;

import org.springframework.transaction.annotation.Transactional;

import com.honey.dto.BoardDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

@Transactional
public interface BoardService {

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
	
}
