package com.honey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.honey.dto.BoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.BoardService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/board")
public class BoardController {

	private final BoardService service;
	private final CustomFileUtil fileUtil;

	// =========================
	// 게시글 조회
	// =========================
	@GetMapping("/{boardNo}")
	public BoardDTO getBoard(@PathVariable Integer boardNo) {
		return service.get(boardNo);
	}

	// =========================
	// 게시글 등록
	// =========================
	@PostMapping("/")
	public Map<String, Integer> register(BoardDTO boardDTO) {

		log.info("Board register: " + boardDTO);

		// 파일 저장
		List<String> fileNames = fileUtil.saveFiles(boardDTO.getFiles());

		if (boardDTO.getFiles() != null && !boardDTO.getFiles().isEmpty()) {
			fileNames = fileUtil.saveFiles(boardDTO.getFiles());
		}

		// DTO에 파일명 세팅
		boardDTO.setUploadFileNames(fileNames);

		Integer boardNo = service.register(boardDTO);

		return Map.of("BOARD_NO", boardNo);
	}

	// =========================
	// 게시글 목록 (페이징)
	// =========================
	@GetMapping("/list")
	public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {
		log.info(searchDTO);
		return service.list(searchDTO);
	}

	// =========================
	// 게시글 수정 (파일 유지/교체 대응)
	// =========================
	@PutMapping("/{boardNo}")
	public Map<String, String> modify(@PathVariable Integer boardNo, BoardDTO boardDTO) {

		boardDTO.setBoardNo(boardNo);

		// 1 기존 파일
		BoardDTO oldDTO = service.get(boardNo);
		List<String> oldFileNames = oldDTO.getUploadFileNames();

		// 2️ 새 파일 저장
		List<String> newFileNames = fileUtil.saveFiles(boardDTO.getFiles());

		if (newFileNames == null || newFileNames.isEmpty()) {
			// 파일 없으면 기존 유지
			boardDTO.setUploadFileNames(oldFileNames);
		} else {
			// 새 파일 있으면 교체
			boardDTO.setUploadFileNames(newFileNames);

			// 기존 파일 삭제
			fileUtil.deleteFiles(oldFileNames);
		}

		service.modify(boardDTO);

		return Map.of("RESULT", "SUCCESS");
	}

	// =========================
	// 게시글 삭제 (논리삭제 + 파일삭제)
	// =========================
	@PutMapping("remove/{boardNo}")
	public Map<String, String> remove(@PathVariable Integer boardNo) {

		// 기존 파일 가져오기
		BoardDTO dto = service.get(boardNo);

		// DB 논리삭제
		service.remove(boardNo);

		// 파일 삭제
		fileUtil.deleteFiles(dto.getUploadFileNames());

		return Map.of("RESULT", "SUCCESS");
	}

	// =========================
	// 파일 조회 (이미지 출력)
	// =========================
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}
}