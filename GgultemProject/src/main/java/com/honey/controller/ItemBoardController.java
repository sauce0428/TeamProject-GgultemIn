package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.ItemBoardDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.service.ItemBoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/honey/itemBoard")
public class ItemBoardController {

	private final ItemBoardService service;
	
	@GetMapping("/{id}")
	public ItemBoardDTO getItemBoard(@PathVariable(name = "id") Long id) {
		return service.get(id);
	}
	
	@PostMapping("/")
	public Map<String,Long> register(ItemBoardDTO itemBoardDTO){
		Long id = service.register(itemBoardDTO);
		return Map.of("id",id);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<ItemBoardDTO> list(PageRequestDTO pageRequestDTO){
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
