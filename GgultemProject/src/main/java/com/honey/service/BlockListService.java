package com.honey.service;

import com.honey.dto.BlockListDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface BlockListService {

	BlockListDTO get(Long no);

	Long register(BlockListDTO blockListDTO);

	PageResponseDTO<BlockListDTO> list(SearchDTO searchDTO);

	void modify(BlockListDTO blockListDTO);

	void remove(Long no);





	
	
}
