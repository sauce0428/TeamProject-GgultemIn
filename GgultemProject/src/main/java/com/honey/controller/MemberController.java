package com.honey.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.MemberService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/member")
public class MemberController {

	private final MemberService service;
	private final CustomFileUtil fileUtil;

	@GetMapping("/{email}")
	public Map<String, Object> getMember(@PathVariable(name = "email") String email) {
		// 🚩 MemberDTO를 통째로 리턴하지 말고, 필요한 데이터만 담긴 Map을 리턴하세요!
		MemberDTO memberDTO = service.get(email);
		return memberDTO.getClaims();
	}

//	@PostMapping("/")
//	public Map<String, String> register(MemberDTO memberDTO) {
//		log.info("여기입니다 =-==================="+memberDTO.toString());
//		String email = service.register(memberDTO);
//		return Map.of("EAMIL", email);
//	}

	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> register(
	        @RequestParam("email") String email, 
	        @RequestParam("pw") String pw,
	        @RequestParam("nickname") String nickname, 
	        @RequestParam("phone") String phone,
	        @RequestParam(value = "files", required = false) List<MultipartFile> files) {

	    log.info("회원 등록 시도: " + email);

	    try {
	        MemberDTO memberDTO = new MemberDTO(email, pw, nickname, false, null, null);
	        memberDTO.setPhone(phone);

	        if (files != null && !files.isEmpty()) {
	            List<String> currentUploadFileNames = fileUtil.saveFiles(files);
	            memberDTO.setUploadFileNames(currentUploadFileNames);
	        }

	        String email_ = service.register(memberDTO);

	        // 🚩 명시적으로 200 OK와 JSON 바디를 리턴
	        Map<String, String> result = new HashMap<>();
	        result.put("RESULT", "SUCCESS");
	        result.put("USER", email_);
	        
	        return ResponseEntity.ok(result);

	    } catch (Exception e) {
	        log.error("등록 중 에러: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	

	@GetMapping("/list")
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		return service.list(searchDTO);
	}

	@PutMapping("/{email}")
	public Map<String, String> modify(@PathVariable(name = "email") String email, MemberDTO memberDTO) {
		log.info("수정 요청 데이터: " + memberDTO);
		memberDTO.setEmail(email);
		service.modify(memberDTO);
		return Map.of("RESULT", "SUCCESS");
	}

	@PutMapping("/remove/{email}")
	public Map<String, String> remove(@PathVariable(name = "email") String email) {
		service.remove(email);
		return Map.of("RESULT", "SUCCESS");
	}

}
