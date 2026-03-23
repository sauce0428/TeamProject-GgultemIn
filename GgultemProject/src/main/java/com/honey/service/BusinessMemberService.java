package com.honey.service;

import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface BusinessMemberService {

	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO);

	public void memberBusinessRegister(MemberDTO MemberDTO);

	public void approve(BusinessMemberDTO bMemberDTO);

	public void modify(BusinessMemberDTO bMemberDTO);

	public boolean verifyBusinessNumber(String cleanBNo);

	public MemberDTO get(String email);

}
