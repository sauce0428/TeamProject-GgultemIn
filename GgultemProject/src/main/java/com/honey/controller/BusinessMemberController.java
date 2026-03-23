package com.honey.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.BusinessMemberRegisterDTO;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.BusinessMemberService;
import com.honey.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/businessmember")
public class BusinessMemberController {
	
	private final BusinessMemberService businessService;
	private final MemberService memberService;
	
	@GetMapping("/{email}")
	public Map<String, Object> getMember(@PathVariable(name = "email") String email) {
		// 🚩 MemberDTO를 통째로 리턴하지 말고, 필요한 데이터만 담긴 Map을 리턴하세요!
		MemberDTO memberDTO = businessService.get(email);
		return memberDTO.getClaims();
	}
	
	@PostMapping("/")
	public Map<String, String> businessMemberRegister(@RequestBody BusinessMemberRegisterDTO regDTO) {
		
		MemberDTO memberDTO = memberService.get(regDTO.getEmail());
		
		if (memberDTO == null) {
	        return Map.of("ERROR", "USER_NOT_FOUND");
	    }
		
		memberDTO.setBusinessNumber(regDTO.getBusinessNumber());
	    memberDTO.setCompanyName(regDTO.getCompanyName());
		
		businessService.memberBusinessRegister(memberDTO);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@PostMapping("/verify")
	public ResponseEntity<Map<String, Boolean>> verifyBusiness(@RequestBody Map<String, String> request) {
	    String bNo = request.get("businessNumber");
	    // 하이픈(-)이 포함되어 있다면 제거하는 로직이 필요할 수 있습니다.
	    String cleanBNo = bNo.replaceAll("-", "");
	    
	    boolean isValid = businessService.verifyBusinessNumber(cleanBNo);
	    
	    return ResponseEntity.ok(Map.of("isValid", isValid));
	}
	
	@GetMapping("/list")
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		return businessService.list(searchDTO);
	}
	
}
