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

import com.honey.dto.CodeDetailDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.CodeDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/codedetail")
public class CodeDetailController {

	private final CodeDetailService service;
	
	@GetMapping("/{groupCode}")
	public CodeDetailDTO getCodeDetail(@PathVariable(name = "groupCode") String groupCode) {
		return service.get(groupCode);
	}
	
	@PostMapping("/")
	public Map<String, String> register(@RequestBody CodeDetailDTO codeDetailDTO) {
		String groupCode = service.register(codeDetailDTO);
		return Map.of("groupCode", groupCode);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<CodeDetailDTO> list(SearchDTO searchDTO) {
		return service.list(searchDTO);
	}
	
	@PutMapping("/{groupCode}")
	public Map<String, String> modify(@PathVariable(name = "groupCode") String groupCode, @RequestBody CodeDetailDTO codeDetailDTO) {
		codeDetailDTO.setGroupCode(groupCode);
		service.modify(codeDetailDTO);
		return Map.of("RESULT", "SUCCESS");
	}
	
	@DeleteMapping("/remove/{groupCode}")
	public Map<String, String> remove(@PathVariable(name = "groupCode") String groupCode) {
		service.remove(groupCode);
		return Map.of("RESULT", "SUCCESS");
	}
	
}
	   


