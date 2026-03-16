package com.honey.service;

import com.honey.dto.CartDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;

public interface CartService {

	public CartDTO get(Long id);

	public Long register(CartDTO cartDTO);

	public PageResponseDTO<CartDTO> list(PageRequestDTO pageRequestDTO);

}
