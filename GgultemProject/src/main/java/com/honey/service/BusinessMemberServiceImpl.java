package com.honey.service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.honey.domain.Member;
import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BusinessMemberRepository;
import com.honey.repository.MemberRepository;
import com.honey.util.CustomFileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BusinessMemberServiceImpl implements BusinessMemberService {
	
	private final ModelMapper modelMapper;
	private final BusinessMemberRepository bMemberRepository;
	private final MemberRepository memberRepository;
	private final CustomFileUtil fileUtil;
	
	@Value("${com.honey.business.api.key}")
	private String businessKey;

	@Override
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("regDate").descending());
		
		Page<Member> result = null;
		if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			result = bMemberRepository.searchByCondition(
					searchDTO.getSearchType(),
					searchDTO.getKeyword(),
					pageable);
		} else {
			result = bMemberRepository.findAll(pageable);
		}
		
		List<MemberDTO> dtoList = result.getContent().stream().map(member -> {
			MemberDTO dto = new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(), member.isSocial(),
					member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()),
					member.getRegDate());
			
			dto.setBusinessNumber(member.getBusinessNumber());
			dto.setCompanyName(member.getCompanyName());
			dto.setBizMoney(member.getBizMoney());
			dto.setBusinessVerified(false);
			return dto; // 반드시 DTO를 리턴해야 합니다!
	    }).collect(Collectors.toList());
		
			long totalCount = result.getTotalElements();
		
			PageResponseDTO<MemberDTO> responseDTO = PageResponseDTO.<MemberDTO>withAll().dtoList(dtoList)
					.pageRequestDTO(searchDTO).totalCount(totalCount).build();

			return responseDTO;
	}
	
	public boolean verifyBusinessNumber(String businessNumber) {
	    String serviceKey = businessKey;
	    String url = "https://api.odcloud.kr/api/nts-businessman/v1/status";

	 // 🚩 2. RestTemplate의 자동 인코딩을 꺼야 키가 변조되지 않습니다!
	    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(url);
	    factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE); // 인코딩 모드 끄기

	    RestTemplate restTemplate = new RestTemplate();
	    restTemplate.setUriTemplateHandler(factory);
	    
	 // 🚩 3. URI 객체를 직접 생성 (serviceKey 포함)
	    URI uri = UriComponentsBuilder.fromUriString(url)
	            .queryParam("serviceKey", serviceKey)
	            .build(true) // 이미 인코딩된 상태라면 true, 아니면 false
	            .toUri();

	    // 🚩 국세청 API 규격에 맞게 JSON 바디 구성
	    Map<String, Object> requestBody = new HashMap<>();
	 // 호출하기 직전에 숫자만 남기기
	    String cleanBNo = businessNumber.replaceAll("-", ""); 
	    requestBody.put("b_no", new String[]{cleanBNo});

	    try {
	    	Map<String, Object> response = restTemplate.postForObject(uri, requestBody, Map.class);
	        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
	        
	        // 🚩 b_stt_cd가 "01"이면 계속 사업을 하고 있다는 뜻입니다.
	        String status = (String) data.get(0).get("b_stt_cd");
	        return "01".equals(status);
	    } catch (Exception e) {
	        log.error("API 호출 에러: " + e.getMessage());
	        return false;
	    }
	}

	@Override
	public void memberBusinessRegister(MemberDTO MemberDTO) {
		
		String email = MemberDTO.getEmail();
		String businessNumber = MemberDTO.getBusinessNumber();
		String companyName = MemberDTO.getCompanyName();
		
		memberRepository.businessRegister(businessNumber, companyName, email);
	}

	@Override
	public void approve(BusinessMemberDTO bMemberDTO) {
		//Optional<BusinessMember> result = bMemberRepository.findById(bMemberDTO.getNo());
//		BusinessMember bMember = result.orElseThrow();
//		
//		bMember.changeStatus(1);
//		bMember.addRole("ROLE_BUSINESS_MEMBER");
		
		//bMemberRepository.save(bMember);
	}

	@Override
	public void modify(BusinessMemberDTO bMemberDTO) {
		//Optional<BusinessMember> result = bMemberRepository.findById(bMemberDTO.getNo());
//		BusinessMember bMember = result.orElseThrow();
//		
//		bMember.changePw(bMemberDTO.getPw());
		
		//bMemberRepository.save(bMember);
	}
	
	

}
