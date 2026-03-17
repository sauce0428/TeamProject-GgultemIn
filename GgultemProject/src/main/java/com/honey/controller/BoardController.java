//package com.honey.controller;
//
//import java.util.Map;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.honey.dto.BoardDTO;
//import com.honey.dto.PageResponseDTO;
//import com.honey.dto.SearchDTO;
//import com.honey.service.BoardService;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//
//@RestController
//@RequiredArgsConstructor
//@Log4j2
//@RequestMapping("/board")
//public class BoardController {
//
//	private final BoardService service;
//
//	// 게시글 조회
//	@GetMapping("/{boardNo}")
//	public BoardDTO getBoard(@PathVariable(name = "boardNo") Integer boardNo) {
//		return service.get(boardNo);
//	}
//
//	// 게시글 등록
//	@PostMapping("/")
//	public Map<String, Integer> register(BoardDTO boardDTO) {
//
//		log.info("Board register: " + boardDTO);
//
//		Integer boardNo = service.register(boardDTO);
//
//		return Map.of("BOARD_NO", boardNo);
//	}
//
//	// 게시글 목록 (페이징)
//	@GetMapping("/list")
//	public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {
//		log.info(searchDTO);
//		return service.list(searchDTO);
//	}
//
//	// 게시글 수정
//	@PutMapping("/{boardNo}")
//	public Map<String, String> modify(@PathVariable(name = "boardNo") Integer boardNo, BoardDTO boardDTO) {
//
//		boardDTO.setBoardNo(boardNo);
//
//		service.modify(boardDTO);
//
//		return Map.of("RESULT", "SUCCESS");
//	}
//
//	// 게시글 삭제
//	@GetMapping("/delete/{boardNo}")
//	public Map<String, String> remove(@PathVariable(name = "boardNo") Integer boardNo) {
//		service.remove(boardNo);
//
//		return Map.of("RESULT", "SUCCESS");
//	}
//
//}