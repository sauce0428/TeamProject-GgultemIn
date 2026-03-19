package com.honey.service;

import com.honey.dto.CodeDetailDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface CodeDetailService {

	CodeDetailDTO get(String groupCode);

	String register(CodeDetailDTO codeDetailDTO);

	PageResponseDTO<CodeDetailDTO> list(SearchDTO searchDTO);

	void modify(CodeDetailDTO codeDetailDTO);

	void remove(String groupCode);

}
