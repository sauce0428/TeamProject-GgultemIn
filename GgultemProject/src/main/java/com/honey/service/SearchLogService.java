package com.honey.service;

import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.dto.SearchLogDTO;

public interface SearchLogService {
	
	public void logSearch(SearchDTO searchDTO);

	public PageResponseDTO<SearchLogDTO> list(SearchDTO searchDTO);

	public PageResponseDTO<SearchLogDTO> listRank(SearchDTO searchDTO);
}
