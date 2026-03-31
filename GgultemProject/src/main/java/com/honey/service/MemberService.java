package com.honey.service;

import com.honey.dto.MemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface MemberService {

	public MemberDTO get(String email);

	public String register(MemberDTO memberDTO);

	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO);

	public void modify(MemberDTO memberDTO);

	public void remove(String email);

	public void updateToThumbnail(MemberDTO memberDTO);

	public MemberDTO getKakaoMember(String code);

	public MemberDTO getMemberWithGoogle(String accessToken);

	public boolean existsByEmail(String email);
	
	public boolean existsByNickname(String nickname);

}
