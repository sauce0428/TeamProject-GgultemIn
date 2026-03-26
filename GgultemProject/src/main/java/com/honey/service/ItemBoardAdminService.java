package com.honey.service;

import com.honey.dto.ItemBoardAdminDTO;
import com.honey.dto.ItemBoardDTO;
import com.honey.dto.ItemBoardSearchDTO;
import com.honey.dto.PageResponseDTO;

public interface ItemBoardAdminService {

    public ItemBoardAdminDTO get(Long id);

    public Long register(ItemBoardDTO itemBoardDTO);

    public PageResponseDTO<ItemBoardAdminDTO> list(ItemBoardSearchDTO searchDTO);

    public void remove(Long id);

	public void soldOut(Long id);

}