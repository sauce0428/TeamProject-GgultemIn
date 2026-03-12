package com.honey.service;

import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;

public interface BusinessMemberService {

	public BusinessMemberDTO get(Long no);

	public PageResponseDTO<BusinessMemberDTO> list(PageRequestDTO pageRequestDTO);

	public Long register(BusinessMemberDTO businessMemberDTO);

	public void modify(BusinessMemberDTO bMemberDTO);

	public void remove(Long no);

}
