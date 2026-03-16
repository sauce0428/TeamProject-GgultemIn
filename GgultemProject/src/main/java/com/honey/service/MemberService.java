package com.honey.service;

import com.honey.dto.MemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface MemberService {

	public MemberDTO get(Long no);

	public Long register(MemberDTO memberDTO);

	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO);

	public void modify(MemberDTO memberDTO);

	public void remove(Long no);

	public void updateToThumbnail(MemberDTO memberDTO);

}
