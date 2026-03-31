package com.honey.security;

import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.honey.domain.Member;
import com.honey.dto.MemberDTO;
import com.honey.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.getWithRoles(username); 
		  if (member == null) { 
		   throw new UsernameNotFoundException("Not Found"); 
		  }
		  
		    // member.getEnabled()가 0이면 탈퇴한 회원이므로 예외 발생
		    if (member.getEnabled() == 0) {
		        log.info("탈퇴한 회원 로그인 시도 차단: " + username);
		        throw new RuntimeException("DELETED_USER");
		    } else if (member.getEnabled() == 2 || member.getEnabled() == 3 || member.getEnabled() == 4) {
	        	log.info("정지된 회원입니다. 로그인을 차단합니다: " + username);
	            // 에러를 던져서 컨트롤러가 리액트에 에러를 보내게 합니다.
	            throw new RuntimeException("STOP_USER");
	        }
		  
		  MemberDTO memberDTO =  
		    new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(),  
		      member.isSocial(), 
		member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()),member.getRegDate()); 
		  return memberDTO; 
	} 

}
