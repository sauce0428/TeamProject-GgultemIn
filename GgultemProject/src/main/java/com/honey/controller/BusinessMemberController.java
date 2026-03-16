package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.BusinessMemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/businessmember")
public class BusinessMemberController {
	
	private final BusinessMemberService service;
	
	@GetMapping("/{no}")
	public BusinessMemberDTO getBusinessMember(@PathVariable(name = "no") Long no) {
		return service.get(no);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<BusinessMemberDTO> list(SearchDTO searchDTO) {
		return service.list(searchDTO);
	}
	
	@PostMapping("/register")
	public Map<String, Long> register(@RequestBody BusinessMemberDTO businessMemberDTO) {
		Long no = service.register(businessMemberDTO);
		return Map.of("NO", no);
	}
	
	@GetMapping("/approve/{no}") // 비즈니스 회원 승인로직
	public Map<String, String> approve(@PathVariable(name = "no") Long no, BusinessMemberDTO bMemberDTO) {
		bMemberDTO.setNo(no);
		service.approve(bMemberDTO);
		return Map.of("RESULT", "SUCCESS");
	}
	
	@PutMapping("/modify/{no}") // 비즈니스 회원 비밀번호 변경
	public Map<String, String> modify(@PathVariable(name = "no") Long no, BusinessMemberDTO bMemberDTO) {
		bMemberDTO.setNo(no);
		service.modify(bMemberDTO);
		return Map.of("RESULT", "SUCCESS");
	}
	
	@GetMapping("/delete/{no}")
	public Map<String, String> remove(@PathVariable(name = "no") Long no) {
		service.remove(no);
		return Map.of("RESULT", "SUCCESS");
	}
	
	
}
