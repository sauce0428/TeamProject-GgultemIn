package com.honey.service;

import com.honey.dto.CartDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface CartService {

    public CartDTO get(Long id);

    public Long register(CartDTO cartDTO);

    public PageResponseDTO<CartDTO> list(SearchDTO searchDTO, String email);

    public void remove(Long id);

	public void removeByItemIdAndEmail(Long itemId, String email);

}