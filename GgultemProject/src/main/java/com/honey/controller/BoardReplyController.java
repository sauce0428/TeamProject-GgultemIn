package com.honey.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.honey.dto.BoardReplyDTO;
import com.honey.service.BoardReplyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reply")
@RequiredArgsConstructor
public class BoardReplyController {

	private final BoardReplyService boardReplyService;

	// 댓글 목록 조회
	@GetMapping("/list/{boardNo}")
	public List<BoardReplyDTO> getList(@PathVariable Integer boardNo) {
		return boardReplyService.getList(boardNo);
	}

	// 댓글 등록
	@PostMapping("/")
	public Long register(@RequestBody BoardReplyDTO dto) {
		return boardReplyService.register(dto);
	}

	// 댓글 수정
	@PutMapping("/{replyNo}")
	public String modify(@PathVariable Long replyNo, @RequestBody BoardReplyDTO dto) {

		dto.setReplyNo(replyNo);

		boardReplyService.modify(dto);

		return "SUCCESS";
	}

	// 댓글 삭제 (논리 삭제)
	@DeleteMapping("/{replyNo}")
	public String remove(@PathVariable Long replyNo) {

		boardReplyService.remove(replyNo);

		return "SUCCESS";
	}
}