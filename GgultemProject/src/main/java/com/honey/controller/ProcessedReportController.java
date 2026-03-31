package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ProcessedReportDTO;
import com.honey.dto.ReportDTO;
import com.honey.service.ProcessedReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
public class ProcessedReportController {

	private final ProcessedReportService processedService;

	@PostMapping("/process")
	public Map<String, Long> process(@RequestBody ProcessedReportDTO dto) {
		Long processedId = processedService.process(dto);
		return Map.of("PROCESSED_ID", processedId);
	}
	
	// ProcessedReportController.java에 추가
	@GetMapping("/list")
	public PageResponseDTO<ReportDTO> list(
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(defaultValue = "10") int size) {
	    PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
	            .page(page)
	            .size(size)
	            .build();
	    return processedService.list(pageRequestDTO);
	}

	@GetMapping("/{reportId}")
	public ReportDTO getOne(@PathVariable Long reportId) {
	    return processedService.getOne(reportId);
	}
	
	@GetMapping("/processed/{reportId}")
	public ProcessedReportDTO getOneProcessed(@PathVariable Long reportId) {
		return processedService.getOneProcessed(reportId);
	}
}