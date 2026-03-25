package com.honey.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.honey.dto.BlackListDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.dto.MemberDTO; // MemberDTO 임포트
import com.honey.service.BlackListService;
import com.honey.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/admin/blacklist") // 리액트 prefix와 일치
@CrossOrigin(origins = "*")
public class BlackListController {

    private final BlackListService service;
    private final MemberService memberService; // 기존 서비스 그대로 사용

    // ✅ 기존 MemberService.get()을 활용한 이메일 체크
    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam("email") String email) {
        log.info("블랙리스트 등록 전 이메일 확인: " + email);
        try {
            // 기존 인터페이스의 get 메소드 활용
            MemberDTO member = memberService.get(email);
            return member != null; // member가 존재하면 true 반환
        } catch (Exception e) {
            // 유저가 없을 때 예외가 발생하는 구조라면 false 반환
            return false;
        }
    }

    @GetMapping("/{blId}")
    public BlackListDTO getBlackList(@PathVariable(name = "blId") Long blId) {
        return service.get(blId);
    }
    
    @PostMapping("/")
    public Map<String, Long> register(@RequestBody BlackListDTO blackListDTO) {
        return Map.of("blId", service.register(blackListDTO));
    }
    
    @GetMapping("/list")
    public PageResponseDTO<BlackListDTO> list(SearchDTO searchDTO) {
        return service.list(searchDTO);
    }
    
    @PutMapping("/{blId}")
    public Map<String, String> modify(@PathVariable(name = "blId") Long blId, @RequestBody BlackListDTO blackListDTO) {
        blackListDTO.setBlId(blId);
        service.modify(blackListDTO);
        return Map.of("RESULT", "SUCCESS");
    }
    
    @DeleteMapping("/{blId}")
    public Map<String, String> remove(@PathVariable(name = "blId") Long blId) {
        service.remove(blId);
        return Map.of("RESULT", "SUCCESS");
    }
}