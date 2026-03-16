package com.honey.service;

import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface BusinessMemberService {

	public BusinessMemberDTO get(Long no);

	public PageResponseDTO<BusinessMemberDTO> list(SearchDTO searchDTO);

	public Long register(BusinessMemberDTO businessMemberDTO);

	public void approve(BusinessMemberDTO bMemberDTO);

	public void remove(Long no);

	public void modify(BusinessMemberDTO bMemberDTO);

}
