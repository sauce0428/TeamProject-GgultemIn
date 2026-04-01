package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.CartDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{id}")
    public CartDTO getCart(Long id) {
        return cartService.get(id);
    }

    @PostMapping("/")
    public Map<String, Long> register(@RequestBody CartDTO cartDTO){
        Long id = cartService.register(cartDTO);
        return Map.of("id", id);
    }

    @GetMapping("/list")
    public PageResponseDTO<CartDTO> list(SearchDTO searchDTO, @RequestParam String email){
        return cartService.list(searchDTO, email);
    }

    @GetMapping("/remove/{id}")
    public Map<String, String> remove(@PathVariable(name="id") Long id){
        cartService.remove(id);
        return Map.of("RESULT","SUCCESS");
    }
    
    // 리스트에서 장바구니 삭제
    @GetMapping("/removeByItem")
    public Map<String, String> removeByItem(
        @RequestParam("itemId") Long itemId, 
        @RequestParam("email") String email) {
        
        log.info("별표 해제 요청 - 상품번호: {}, 이메일: {}", itemId, email);
        
        // 서비스에 이 기능을 만들어야 합니다.
        cartService.removeByItemIdAndEmail(itemId, email); 
        
        return Map.of("RESULT", "SUCCESS");
    }
}