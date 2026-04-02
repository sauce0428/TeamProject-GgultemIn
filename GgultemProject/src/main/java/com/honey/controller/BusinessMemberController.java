package com.honey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.BizMoneyHistoryDTO;
import com.honey.dto.BusinessMemberRegisterDTO;
import com.honey.dto.MemberBizMoneySummary;
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
	
	@GetMapping("/approve/{email}")
	public Map<String, String> approve(@PathVariable(name = "email") String email) {
		businessService.approve(email);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@GetMapping("/reject/{email}")
	public Map<String, String> reject(@PathVariable(name = "email") String email) {
		businessService.reject(email);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@GetMapping("/list")
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		
		if(searchDTO.getBusinessVerified().equals("all")) {
			searchDTO.setBusinessVerified(null);
		}
		
		return businessService.list(searchDTO);
	}
	
	@PostMapping("/admin/charge/confirm")
	public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody Map<String, String> requestData) {
	    // 1. 서비스에서 토스 API 호출 및 DB 업데이트 수행
	    businessService.confirmPayment(
	        requestData.get("paymentKey"),
	        requestData.get("orderId"),
	        requestData.get("email"),
	        Long.parseLong(requestData.get("amount"))
	    );
	    
	    return ResponseEntity.ok(Map.of("RESULT", "SUCCESS"));
	}
	
	@GetMapping("/history/{email}")
	public PageResponseDTO<BizMoneyHistoryDTO> bizMoneyHistory(SearchDTO searchDTO, @PathVariable(name = "email") String email) {
		
		log.info("내가받은 서치디티오 값 : "+searchDTO);
		
		if("all".equals(searchDTO.getState())) {
			searchDTO.setState(null);
		}
		
		return businessService.getBizMoneyHistory(searchDTO, email);	
	}
	
	@GetMapping("/admin/history")
	public PageResponseDTO<BizMoneyHistoryDTO> bizMoneyHistoryAdmin(SearchDTO searchDTO) {
		
		if("all".equals(searchDTO.getState())) {
			searchDTO.setState(null);
		}
		
		return businessService.getBizMoneyHistoryAdmin(searchDTO);	
	}
	
	@GetMapping("/admin/totalhistory")
	public  PageResponseDTO<Map<String, Object>> getBizMoneySummary(SearchDTO searchDTO) {
		if (searchDTO.getKeyword() == null) searchDTO.setKeyword("");
		if ("all".equals(searchDTO.getSearchType())) searchDTO.setSearchType("email");
	    if ("all".equals(searchDTO.getState())) searchDTO.setState(null);
	    return businessService.getBizMoneySummary(searchDTO);
	}
	
	@PutMapping("/spend/{email}")
	public void spendMoneyByClick(@PathVariable(name = "email") String email, Long amount, String title) {
		businessService.spendMoneyByClick(email, amount, title);
	}
	
	@GetMapping("/todaySpend/{email}")
	public Long getTodaySpend(@PathVariable(name = "email") String email) {
		return businessService.getTodaySpend(email);
	}
	
	@GetMapping("/totalSpend/{email}")
	public Long getTotalSpend(@PathVariable(name = "email") String email) {
		return businessService.getTotalSpend(email);
	}
	
	@GetMapping("/todayClick/{email}")
	public Integer getTodayViewCount(@PathVariable(name = "email") String email) {
		return businessService.getTodayViewCount(email);
	}
	
	@GetMapping("/totalClick/{email}")
	public Integer getTotalViewCount(@PathVariable(name = "email") String email) {
		return businessService.getTotalViewCount(email);
	}
	
}
