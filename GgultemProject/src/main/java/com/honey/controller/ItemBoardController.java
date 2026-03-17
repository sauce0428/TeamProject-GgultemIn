package com.honey.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.honey.dto.ItemBoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.ItemBoardService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/itemBoard")
public class ItemBoardController {

	private final ItemBoardService itemBoardService;
	private final CustomFileUtil fileUtil;

	@GetMapping("/{id}")
	public ItemBoardDTO getItemBoard(@PathVariable(name = "id") Long id) {
		return itemBoardService.get(id);
	}

	@PostMapping("/")
	public Map<String, Long> register(ItemBoardDTO itemBoardDTO) {

		// 1. 포스트맨에서 보낸 파일이 서버에 "진짜로" 도착했는지 확인
		List<MultipartFile> files = itemBoardDTO.getFiles();

		// 2. 파일 유틸이 일을 제대로 했는지 확인
		List<String> uploadFileNames = fileUtil.saveFiles(files);

		// 3. DTO에 제대로 세팅했는지 확인
		itemBoardDTO.setUploadFileNames(uploadFileNames);

		Long id = itemBoardService.register(itemBoardDTO);
		return Map.of("id", id);
	}

	@GetMapping("/list")
	public PageResponseDTO<ItemBoardDTO> list(SearchDTO searchDTO) {
		log.info(searchDTO);
		return itemBoardService.list(searchDTO);
	}

	@PutMapping("/{id}")
	public Map<String, String> modify(@PathVariable(name = "id") Long id, ItemBoardDTO itemBoardDTO) {
		itemBoardDTO.setId(id);

		ItemBoardDTO oldItemDTO = itemBoardService.get(id);
		
		List<String> oldFileNames = oldItemDTO.getUploadFileNames();
		
		List<MultipartFile> files = itemBoardDTO.getFiles();
		
		List<String> currentUploadFileNames = null;
		
		if(files != null && !files.get(0).isEmpty()) {
			currentUploadFileNames = fileUtil.saveFiles(files);
		}
		
		List<String> uploadedFileNames = itemBoardDTO.getUploadFileNames();
		
		if(currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
			uploadedFileNames.addAll(currentUploadFileNames);
		}

		itemBoardService.modify(itemBoardDTO);
		
		if(oldFileNames != null && !oldFileNames.isEmpty()) {
			List<String> removeFiles = oldFileNames.stream().filter(
					fileName -> uploadedFileNames.indexOf(fileName) == -1).collect(Collectors.toList());
			fileUtil.deleteFiles(removeFiles);
		}
		return Map.of("RESULT", "SUCCESS");
	}

	@GetMapping("/delete/{id}")
	public Map<String, String> remove(@PathVariable(name = "id") Long id) {
		List<String> oldFileNames = itemBoardService.get(id).getUploadFileNames();
		itemBoardService.remove(id);
		
		fileUtil.deleteFiles(oldFileNames);
		
		return Map.of("RESULT", "SUCCESS");
	}

}
