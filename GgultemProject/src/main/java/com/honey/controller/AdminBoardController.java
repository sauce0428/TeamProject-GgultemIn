package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.BoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/board")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AdminBoardController {
	private final BoardService boardService;

	// 게시글 전체조회 (삭제된 게시물포함)
	@GetMapping("/list")
	public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {
		return boardService.adminList(searchDTO);
	}

	// 게시글 상세 + 댓글 같이 보기
	@GetMapping("/{boardNo}")
	public BoardDTO read(@PathVariable Integer boardNo) {
		return boardService.get(boardNo);
	}
	
	@GetMapping("/reply/list")
	public PageResponseDTO<?> replyList(SearchDTO searchDTO) {
	    return boardService.adminReplyList(searchDTO);
	}

	// 게시글 강제 삭제
	@PutMapping("/{boardNo}")
	public Map<String, String> adminRemove(@PathVariable Integer boardNo) {
	    boardService.adminRemove(boardNo);
	    return Map.of("result", "success");
	}

	// 댓글 삭제
	@PutMapping("/reply/{replyNo}")
	public Map<String, String> adminRemoveReply(@PathVariable Long replyNo) {
	    boardService.removeReply(replyNo);
	    return Map.of("result", "success");
	}
}
