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

	private final BoardReplyService service;

	// 댓글 목록 조회
	@GetMapping("/list/{boardNo}")
	public List<BoardReplyDTO> list(@PathVariable Integer boardNo) {
		return service.list(boardNo);
	}

	// 댓글 등록
	@PostMapping("/")
	public Long register(@RequestBody BoardReplyDTO dto) {
		return service.register(dto);
	}

	// 댓글 수정
	@PutMapping("/{replyNo}")
	public String modify(@PathVariable Long replyNo, @RequestBody BoardReplyDTO dto) {

		dto.setReplyNo(replyNo);

		service.modify(dto);

		return "SUCCESS";
	}

	// 댓글 삭제 
	@GetMapping("/{replyNo}")
	public String remove(@PathVariable Long replyNo) {

		service.remove(replyNo);

		return "SUCCESS";
	}
}