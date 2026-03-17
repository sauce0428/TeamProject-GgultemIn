package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.BlockListDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.BlockListService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/blocklist")
public class BlockListController {

	private final BlockListService service;
	
	@GetMapping("/{no}")
	public BlockListDTO getBlockList(@PathVariable(name = "no") Long no) {
		return service.get(no);
	}
	
	@PostMapping("/")
	public Map<String, Long> register(@RequestBody BlockListDTO blockListDTO) {
		Long no = service.register(blockListDTO);
		return Map.of("no", no);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<BlockListDTO> list(SearchDTO searchDTO) {
		return service.list(searchDTO);
	}
	
	@PutMapping("/{no}")
	public Map<String, String> modify(@PathVariable(name = "no") Long no, @RequestBody BlockListDTO blockListDTO) {
		blockListDTO.setNo(no);
		service.modify(blockListDTO);
		return Map.of("RESULT", "SUCCESS");
	}
	
	@DeleteMapping("/remove/{no}")
	public Map<String, String> remove(@PathVariable(name = "no") Long no) {
		service.remove(no);
		return Map.of("RESULT", "SUCCESS");
	}
	
}
	   


