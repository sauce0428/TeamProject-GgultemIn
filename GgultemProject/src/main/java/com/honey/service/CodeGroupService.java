package com.honey.service;

import com.honey.dto.CodeGroupDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface CodeGroupService {

	CodeGroupDTO get(Long groupCode);

	Long register(CodeGroupDTO codeGroupDTO);

	PageResponseDTO<CodeGroupDTO> list(SearchDTO searchDTO);

	void modify(CodeGroupDTO codeGroupDTO);

	void remove(Long groupCode);

	




	
	
}
