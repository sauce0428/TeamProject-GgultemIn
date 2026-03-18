package com.honey.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.honey.domain.Member;
import com.honey.domain.MemberRole;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.MemberRepository;
import com.honey.util.CustomFileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final ModelMapper modelMapper;
	private final MemberRepository memberRepository;
	private final SearchLogService searchLogSearvice;
	private final CustomFileUtil fileUtil;
	private final PasswordEncoder passwordEncoder; // 주입 필요

	@Override
	public MemberDTO get(String email) {
		Member member = memberRepository.findById(email).orElseThrow();

		MemberDTO memberDTO =  
			    new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(),  
			      member.isSocial(), 
			member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()),member.getRegDate());

		List<String> fileNameList = member.getThumbnailList().stream().map(thumbnail -> thumbnail.getFileName())
				.collect(Collectors.toList());

		if (fileNameList != null && !fileNameList.isEmpty()) {
			memberDTO.setUploadFileNames(fileNameList);
		} else {
			memberDTO.setUploadFileNames(List.of("default.jpg"));
		}

		return memberDTO;
	}

	@Override
	public String register(MemberDTO memberDTO) {
		Member member = modelMapper.map(memberDTO, Member.class);
		
		member.changePw(passwordEncoder.encode(memberDTO.getPw())); // 암호화
		member.changeStatus(1);
		member.addRole(MemberRole.MEMBER);

		Member savedMember = memberRepository.save(member);

		return savedMember.getEmail();
	}

	@Override
	public void modify(MemberDTO memberDTO) {
		Member member = memberRepository.findById(memberDTO.getEmail()).orElseThrow();

		// 1. 비밀번호: 넘어온 값이 있을 때만 수정 (암호화는 필수!)
	    if (memberDTO.getPw() != null && !memberDTO.getPw().isEmpty()) {
	        // 비밀번호를 수정할 경우 반드시 암호화해서 넣어야 로그인이 됩니다.
	        member.changePw(passwordEncoder.encode(memberDTO.getPw())); 
	        log.info("비밀번호 변경됨");
	    } else {
	        log.info("비밀번호 변경 안 함 (기존 유지)");
	    }
		member.changePhone(memberDTO.getPhone());
		member.changeNickName(memberDTO.getNickname());
		
		log.info("수정된 데이터 =" + member.toString());
		
	    if (memberDTO.getEnabled() != null) {
	        if (!member.getEnabled().equals(memberDTO.getEnabled())) {
	            member.changeStatus(memberDTO.getEnabled());
	        }
	    }

		memberRepository.save(member);
	}

	@Override
	public void remove(String email) {
		Member member = memberRepository.findById(email).orElseThrow();

		member.changeStatus(0);
		
		List<String> oldFileNames = member.getThumbnailList().stream()
	            .map(thumbnail -> thumbnail.getFileName())
	            .collect(Collectors.toList());
	    
	    if (oldFileNames != null && !oldFileNames.isEmpty()) {
	        fileUtil.deleteFiles(oldFileNames);
	    }

	    member.clearList();

		memberRepository.save(member);
	}

	@Override
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("regDate").descending());
		
		Page<Member> result = null;
		if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			//searchLogSearvice.logSearch(searchDTO);
			result = memberRepository.searchByCondition(
					searchDTO.getSearchType(),
					searchDTO.getKeyword(),
					pageable);
		} else {
			result = memberRepository.findAll(pageable);
		}
		
		List<MemberDTO> dtoList = result.getContent().stream().map(member -> {
	        MemberDTO dto = new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(), member.isSocial(), 
	        		member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()), member.getRegDate());
	        
	        
	        List<String> fileNameList = member.getThumbnailList().stream()
	                .map(thumbnail -> thumbnail.getFileName())
	                .collect(Collectors.toList());

	        if (fileNameList != null && !fileNameList.isEmpty()) {
	            dto.setUploadFileNames(fileNameList);
	        } else {
	            dto.setUploadFileNames(List.of("default.jpg"));
	        }

	        return dto;
	    }).collect(Collectors.toList());

	long totalCount = result.getTotalElements();

	PageResponseDTO<MemberDTO> responseDTO = PageResponseDTO.<MemberDTO>withAll().dtoList(dtoList)
			.pageRequestDTO(searchDTO).totalCount(totalCount).build();

	return responseDTO;
	}

	@Override
	public void updateToThumbnail(MemberDTO memberDTO) {
	    Member member = memberRepository.findById(memberDTO.getEmail()).orElseThrow();

	    member.clearList();
	    
	    log.info("저장된 이름 = "+memberDTO.getUploadFileNames().toString());
	    
	    List<String> newFileNames = memberDTO.getUploadFileNames();
	    if (newFileNames != null && !newFileNames.isEmpty()) {
	        newFileNames.forEach(fileName -> {
	            member.addImageString(fileName);
	        });
	    }

	    memberRepository.save(member);
	}
	
	
	// 카카오 소셜 로그인 메인 로직
    @Override
    public MemberDTO getKakaoMember(String code) {
    	// 1. 액세스 토큰 받기 (이미 구현된 부분)
        String accessToken = getAccessToken(code);

        // 2. 중요! 이 토큰을 가지고 '카카오 유저 정보 API'를 호출해야 합니다.
        String userInfoJson = getUserInfoFromKakao(accessToken);
        
        // 2. 액세스 토큰으로 이메일 정보 가져오기
        String email = getMemberEmail(userInfoJson); 
        
        log.info("카카오 계정 이메일: " + email);
        
        // 3. 우리 DB에 있는 회원인지 확인
        Optional<Member> result = memberRepository.findById(email);
        
        if(result.isPresent()) {
            // 이미 가입된 회원이면 바로 DTO 변환 (로그인 처리)
            return entityToDTO(result.get());
        }

        // 4. 없는 회원이면 자동 회원가입 진행
        Member member = Member.builder()
                .email(email)
                .pw(passwordEncoder.encode("1111")) // 소셜용 임시 비번
                .nickname("KakaoUser_" + email.substring(6).split("@")[0])
                .social(true)
                .build();
        
        member.addRole(MemberRole.USER); // 기본 권한 부여
        memberRepository.save(member);
        
        return entityToDTO(member);
    }

    // 인가 코드를 카카오 서버에 보내서 Access Token을 받아오는 로직
    private String getAccessToken(String code) {
        String kakaoTokenUrl = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "926c20ad6655d0951df3feb3788f0d99"); 
        params.add("redirect_uri", "http://localhost:5173/member/kakao");
        params.add("client_secret", "xWuBwDKdzO6WVAXKhgWzrnJS0yEw4E7w");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            kakaoTokenUrl, HttpMethod.POST, kakaoTokenRequest, String.class
        );
        
     // ✅ 이 로그가 찍히는 내용을 확인해야 합니다!
        log.info("카카오 토큰 요청 결과: " + response.getBody());

        // JSON 파싱해서 access_token 추출
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("카카오 토큰 파싱 에러", e);
            return null;
        }
    }

    // ⭐️ 여기에 entityToDTO를 직접 만드시면 됩니다!
    private MemberDTO entityToDTO(Member member) {
        MemberDTO dto = new MemberDTO(
            member.getEmail(),
            member.getPw(),
            member.getNickname(),
            member.isSocial(),
            member.getMemberRoleSet().stream()
                  .map(role -> role.name())
                  .collect(Collectors.toSet()),
            member.getRegDate()
        );
        
        // 썸네일 파일 이름들도 담아주기 (기존 get 메서드 로직 참고)
        List<String> fileNames = member.getThumbnailList().stream()
                .map(thumbnail -> thumbnail.getFileName())
                .collect(Collectors.toList());
        
        if(fileNames.isEmpty()) {
            dto.setUploadFileNames(List.of("default.jpg"));
        } else {
            dto.setUploadFileNames(fileNames);
        }

        return dto;
    }
	
 // 기존의 getEmailFromKakao 대신 사용할 메서드
    private String getMemberEmail(String responseBody) {
    	// ✅ 이 로그를 꼭 찍어서 콘솔에 뭐가 나오는지 보세요!
        log.info("--- 카카오에서 받은 실제 데이터: " + responseBody);
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // ✅ 카카오 고유 ID (이건 무조건 있습니다)
            String id = jsonNode.get("id").asText(); 
            
            // ✅ 닉네임 추출 (properties 안에 있습니다)
            // 만약 이것도 없으면 "Guest"로 대체하도록 안전하게 작성
            JsonNode properties = jsonNode.get("properties");
            String nickname = (properties != null && properties.get("nickname") != null) 
                              ? properties.get("nickname").asText() 
                              : "User";

            // ✅ 이메일 대신 사용할 고유 식별자 생성
            // 공백 제거를 위해 replaceAll 사용
            String fakeEmail = "kakao_" + id + "@kakao.com";
            
            log.info("--- 생성된 식별용 이메일: " + fakeEmail);
            return fakeEmail;
            
        } catch (Exception e) {
            log.error("--- 카카오 데이터 파싱 중 치명적 에러: " + e.getMessage());
            throw new RuntimeException("카카오 정보 추출 실패!");
        }
    }
    
 // ✅ 새로 추가해야 할 메서드 예시
    private String getUserInfoFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken); // 토큰 헤더 추가
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );
        
        return response.getBody(); // 여기서 비로소 닉네임과 ID가 든 JSON이 나옵니다!
    }
	
}
