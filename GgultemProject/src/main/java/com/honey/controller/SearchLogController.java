package com.honey.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.dto.SearchLogDTO;
import com.honey.service.SearchLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/searchrank")
public class SearchLogController {
	
	private final SearchLogService service;
	
	@GetMapping("/list")
	public PageResponseDTO<SearchLogDTO> list(SearchDTO searchDTO) {
		return service.list(searchDTO);
	}
	
	@GetMapping("/list/rank")
	public PageResponseDTO<SearchLogDTO> listRank(SearchDTO searchDTO) {
		return service.listRank(searchDTO);
	}
}
