package com.honey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.honey.dto.ItemBoardAdminDTO;
import com.honey.dto.ItemBoardDTO;
import com.honey.dto.ItemBoardSearchDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.service.ItemBoardAdminService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/itemBoard")
public class ItemBoardAdminController {

	private final ItemBoardAdminService service;
	private final CustomFileUtil fileUtil;

	@GetMapping("/{id}")
	public ItemBoardAdminDTO getItemBoardAdmin(@PathVariable(name = "id") Long id) {
		return service.get(id);
	}

	@PostMapping("/")
	public Map<String, Long> register(ItemBoardDTO dto) {
		List<MultipartFile> files = dto.getFiles();

		// 2. 파일 유틸이 일을 제대로 했는지 확인
		List<String> uploadFileNames = fileUtil.saveFiles(files);

		// 3. DTO에 제대로 세팅했는지 확인
		dto.setUploadFileNames(uploadFileNames);
		Long id = service.register(dto);
		return Map.of("id", id);
	}

	@GetMapping("/list")
	public PageResponseDTO<ItemBoardAdminDTO> list(ItemBoardSearchDTO searchDTO) {
		return service.list(searchDTO);
	}

	@GetMapping("/remove/{id}")
	public Map<String, String> remove(@PathVariable(name = "id") Long id) {
		service.remove(id);

		return Map.of("RESULT", "SUCCESS");
	}
	@GetMapping("/soldOut/{id}")
	public Map<String, String> soldOut(@PathVariable(name = "id") Long id) {
		service.soldOut(id);
		
		return Map.of("RESULT", "SUCCESS");
	}

}