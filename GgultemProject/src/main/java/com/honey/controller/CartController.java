package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.CartDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.service.CartService;
import com.honey.service.ItemBoardService;
import com.honey.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/cart")
public class CartController {

	private final MemberService memberService;
	private final ItemBoardService itemBoardService;
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
	public PageResponseDTO<CartDTO> list(PageRequestDTO pageRequestDTO){
		return cartService.list(pageRequestDTO);
	}
}
