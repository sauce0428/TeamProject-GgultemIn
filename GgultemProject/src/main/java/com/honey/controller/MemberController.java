package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;



@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/member")
public class MemberController {
	
	private final MemberService service;
	
	@GetMapping("/{no}")
	public MemberDTO getMember(@PathVariable(name = "no") Long no) {
		return service.get(no);
	}
	
	@PostMapping("/")
	public Map<String, Long> register(@RequestBody MemberDTO memberDTO) {
		Long no = service.register(memberDTO);
		return Map.of("NO", no);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		return service.list(searchDTO);
	}
	
	@PutMapping("/{no}")
	public Map<String, String> modify(@PathVariable(name = "no") Long no, @RequestBody MemberDTO memberDTO) {
		memberDTO.setNo(no);
		service.modify(memberDTO);
		return Map.of("RESULT", "SUCCESS");
	}

	@PutMapping("/remove/{no}")
	public Map<String, String> remove(@PathVariable(name = "no") Long no) {
		service.remove(no);
		return Map.of("RESULT", "SUCCESS");
	}
	
	
	
	
}
