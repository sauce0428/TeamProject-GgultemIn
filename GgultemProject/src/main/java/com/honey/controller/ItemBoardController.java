package com.honey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.honey.dto.ItemBoardDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.service.ItemBoardService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/honey/itemBoard")
public class ItemBoardController {

	private final ItemBoardService service;
	private final CustomFileUtil fileUtil;

	@GetMapping("/{id}")
	public ItemBoardDTO getItemBoard(@PathVariable(name = "id") Long id) {
		return service.get(id);
	}

	@PostMapping("/")
	public Map<String, Long> register(ItemBoardDTO itemBoardDTO) {

		// 1. 포스트맨에서 보낸 파일이 서버에 "진짜로" 도착했는지 확인
		List<MultipartFile> files = itemBoardDTO.getFiles();
		log.info("1. [컨트롤러] 요청에 포함된 실제 파일 개수: " + (files != null ? files.size() : 0));

		// 2. 파일 유틸이 일을 제대로 했는지 확인
		List<String> uploadFileNames = fileUtil.saveFiles(files);
		log.info("2. [컨트롤러] 파일 유틸이 저장 후 반환한 이름들: " + uploadFileNames);

		// 3. DTO에 제대로 세팅했는지 확인
		itemBoardDTO.setUploadFileNames(uploadFileNames);
		log.info("3. [컨트롤러] 서비스로 넘기기 직전 DTO 상태: " + itemBoardDTO.getUploadFileNames());

		Long id = service.register(itemBoardDTO);
		return Map.of("id", id);
	}

	@GetMapping("/list")
	public PageResponseDTO<ItemBoardDTO> list(PageRequestDTO pageRequestDTO) {
		log.info(pageRequestDTO);
		return service.list(pageRequestDTO);
	}

	@PutMapping("/{id}")
	public Map<String, String> modify(@PathVariable(name = "id") Long id, ItemBoardDTO itemBoardDTO) {
		itemBoardDTO.setId(id);
		service.modify(itemBoardDTO);
		return Map.of("RESULT", "SUCCESS");
	}

	@PutMapping("/delete/{id}")
	public Map<String, String> remove(@PathVariable(name = "id") Long id) {
		service.remove(id);
		return Map.of("RESULT", "SUCCESS");
	}

}
