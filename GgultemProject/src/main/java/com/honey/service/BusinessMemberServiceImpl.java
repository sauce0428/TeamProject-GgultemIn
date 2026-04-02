package com.honey.service;

import java.net.URI;
import java.util.Base64;
import java.util.Collections;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.honey.domain.BizMoneyHistory;
import com.honey.domain.Member;
import com.honey.dto.BizMoneyHistoryDTO;
import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.MemberBizMoneySummary;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BizMoneyHistoryRepository;
import com.honey.repository.BusinessBoardRepository;
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
	private final BizMoneyHistoryRepository bizMoneyHistoryRepository;
	private final BusinessBoardRepository businessBoardRepository;
	
	@Value("${com.honey.business.api.key}")
	private String businessKey;
	
	@Value("${com.honey.business.api.toss.key}")
	private String tossKey;
	
	@Override
	public MemberDTO get(String email) {
		Member member = memberRepository.findById(email).orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

		MemberDTO memberDTO = new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(), member.isSocial(),
				member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()),
				member.getRegDate());
		
		memberDTO.setBusinessNumber(member.getBusinessNumber());
		memberDTO.setCompanyName(member.getCompanyName());
		memberDTO.setBizMoney(member.getBizMoney());
		memberDTO.setBusinessVerified(member.isBusinessVerified());

		List<String> fileNameList = member.getThumbnailList().stream().map(thumbnail -> thumbnail.getFileName())
				.collect(Collectors.toList());

		if (fileNameList != null && !fileNameList.isEmpty()) {
			memberDTO.setUploadFileNames(fileNameList);
		} else {
			memberDTO.setUploadFileNames(List.of("default.jpg"));
		}

		memberDTO.setEnabled(member.getEnabled());
		memberDTO.setPhone(member.getPhone());
		memberDTO.setDtdDate(member.getDtdDate());
		memberDTO.setStopDate(member.getStopDate());
		memberDTO.setStopEndDate(member.getStopEndDate());

		return memberDTO;
	}

	@Override
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("regDate").descending());
		
		Page<Member> result = null;
		if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			
			if(searchDTO.getBusinessVerified() != null) {
				result = bMemberRepository.searchByConditionFilter(
						searchDTO.getSearchType(),
						searchDTO.getKeyword(),
						Boolean.parseBoolean(searchDTO.getBusinessVerified()),
						pageable);
			} else {
				result = bMemberRepository.searchByCondition(
						searchDTO.getSearchType(),
						searchDTO.getKeyword(),
						pageable);
			}
			
		} else if(searchDTO.getBusinessVerified() != null) {
			result = bMemberRepository.findAllBusinessFilter(pageable, Boolean.parseBoolean(searchDTO.getBusinessVerified()));
		} else {
			result = bMemberRepository.findAllBusiness(pageable);
		}
		
		List<MemberDTO> dtoList = result.getContent().stream().map(member -> {
			MemberDTO dto = new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(), member.isSocial(),
					member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()),
					member.getRegDate());
			
			dto.setBusinessNumber(member.getBusinessNumber());
			dto.setCompanyName(member.getCompanyName());
			dto.setBizMoney(member.getBizMoney());
			dto.setBusinessVerified(member.isBusinessVerified());
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

	 // 수정 제안
	    Map<String, Object> requestBody = new HashMap<>();
	    String cleanBNo = businessNumber.replaceAll("-", ""); 
	    requestBody.put("b_no", Collections.singletonList(cleanBNo)); // 명시적인 리스트 전달

	 // HttpHeaders 설정 추가
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    // HttpEntity로 감싸서 보내기
	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

	    try {
	        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(uri, entity, Map.class);
	        Map<String, Object> response = responseEntity.getBody();
	        
	        if (response != null && response.containsKey("data")) {
	            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
	            
	            if (data != null && !data.isEmpty()) {
	                // b_stt_cd가 "01"이면 계속 사업을 하고 있다는 뜻
	                String status = (String) data.get(0).get("b_stt_cd");
	                return "01".equals(status);
	            }
	        }
	        return false; // 데이터가 없거나 형식이 다를 경우
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
	public void approve(String email) {
		Member member = memberRepository.findById(email).orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));		
		
		member.upgradeToBusiness();
		
		bMemberRepository.save(member);
	}
	
	@Override
	public void reject(String email) {
		Member member = memberRepository.findById(email).orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));		
		
		member.downgradeToBusiness();
		
		bMemberRepository.save(member);
	}

	@Override
	public void modify(BusinessMemberDTO bMemberDTO) {
		//Optional<BusinessMember> result = bMemberRepository.findById(bMemberDTO.getNo());
//		BusinessMember bMember = result.orElseThrow();
//		
//		bMember.changePw(bMemberDTO.getPw());
		
		//bMemberRepository.save(bMember);
	}
	
	//비즈니스 회원 비즈머니 충전 로직 =============================
	
	@Transactional
	@Override
	public void chargeMoney(String email, Long amount) {
	    Member member = memberRepository.findById(email).orElseThrow();
	    
	    // ✨ 기존 잔액에 충전 금액 합산
	    long newBalance = (member.getBizMoney() != 0 ? member.getBizMoney() : 0L) + amount;
	    member.changeBizMoney(newBalance);
	    
	    //비즈머니 충전 내역 log 저장
	    BizMoneyHistory history = BizMoneyHistory.builder()
	            .member(member)
	            .amount(amount)
	            .balance(newBalance)
	            .type("CHARGE")
	            .detail("비즈머니 충전")
	            .build();
	    
	    bizMoneyHistoryRepository.save(history);
	}
	
	@Override
	@Transactional
	public void confirmPayment(String paymentKey, String orderId, String email, Long amount) {
	    // 1. 토스 승인 API 호출을 위한 설정
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    
	    String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:"; 
	    String encodedAuth = Base64.getEncoder().encodeToString(secretKey.getBytes());
	    headers.set("Authorization", "Basic " + encodedAuth);
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    Map<String, Object> params = new HashMap<>();
	    params.put("paymentKey", paymentKey);
	    params.put("orderId", orderId);
	    params.put("amount", amount);

	    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

	    try {
	        String url = "https://api.tosspayments.com/v1/payments/confirm";
	        // 🚩 응답 결과를 받아서 확인해야 합니다.
	        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
	        
	        if (response.getStatusCode().is2xxSuccessful()) {
	            log.info("토스 결제 승인 성공!");
	            this.chargeMoney(email, amount); 
	        } else {
	            throw new RuntimeException("토스 승인 응답 실패: " + response.getStatusCode());
	        }

	    } catch (Exception e) {
	        log.error("토스 결제 승인 최종 실패: " + e.getMessage());
	        throw new RuntimeException("결제 승인 중 오류 발생: " + e.getMessage());
	    }
	}
	
	//비즈니스 회원 광고 상품 클릭당비용(CPC) 과금 로직 ================
	@Transactional
	public void spendMoneyByClick(String email, Long cpcAmount, String productName) {
	    Member member = memberRepository.findById(email).orElseThrow();
	    
	    long currentMoney = member.getBizMoney();
	    if (currentMoney < cpcAmount) {
	        // 잔액 부족 시 광고 중단 로직 등을 추가할 수 있음
	        throw new RuntimeException("비즈머니가 부족합니다.");
	    }

	    long nextMoney = currentMoney - cpcAmount;
	    member.changeBizMoney(nextMoney);

	    BizMoneyHistory history = BizMoneyHistory.builder()
	            .member(member)
	            .amount(-cpcAmount) // 지출은 마이너스!
	            .balance(nextMoney)
	            .type("SPEND")
	            .detail("[" + productName + "] 광고 클릭 지출")
	            .build();

	    bizMoneyHistoryRepository.save(history);
	}

	@Override
	public PageResponseDTO<BizMoneyHistoryDTO> getBizMoneyHistory(SearchDTO searchDTO, String email) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("regDate").descending());
		
		Page<BizMoneyHistory> result = null;
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			if (searchDTO.getState() != null) {
				result = bizMoneyHistoryRepository.searchByConditionStateFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						searchDTO.getState(),
						pageable, email);
			} else {
				result = bizMoneyHistoryRepository.searchByConditionAllFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						pageable, email);
			}

		} else if (searchDTO.getState() != null) {
			result = bizMoneyHistoryRepository.findAllBizMoneyAllFilter(pageable, searchDTO.getState(), email);
		} else {
			result = bizMoneyHistoryRepository.findAllBizMoney(pageable, email);
		}

		List<BizMoneyHistoryDTO> dtoList = result.getContent().stream().map(bizMoneyHistory -> {
			BizMoneyHistoryDTO dto = modelMapper.map(bizMoneyHistory, BizMoneyHistoryDTO.class);
			
			return dto;
		}).collect(Collectors.toList());

		return PageResponseDTO.<BizMoneyHistoryDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}

	@Override
	public Long getTodaySpend(String email) {
	    java.time.LocalDateTime startOfToday = java.time.LocalDate.now().atStartOfDay();
	    
		return bizMoneyHistoryRepository.getTodaySpend(email, startOfToday);
	}

	@Override
	public Long getTotalSpend(String email) {
		return bizMoneyHistoryRepository.getTotalSpend(email);
	}
	
	@Override
	public Integer getTodayViewCount(String email) {
		java.time.LocalDateTime startOfToday = java.time.LocalDate.now().atStartOfDay();
		
		return businessBoardRepository.getTodayClick(email, startOfToday);
	}
	
	
	@Override
	public Integer getTotalViewCount(String email) {
		return businessBoardRepository.getTotalClick(email);
	}

	@Override
	public PageResponseDTO<BizMoneyHistoryDTO> getBizMoneyHistoryAdmin(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("member.email").descending());
		
		Page<BizMoneyHistory> result = null;
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			if (searchDTO.getState() != null) {
				result = bizMoneyHistoryRepository.searchByConditionStateFilterAdmin(searchDTO.getSearchType(), searchDTO.getKeyword(),
						searchDTO.getState(),
						pageable);
			} else {
				result = bizMoneyHistoryRepository.searchByConditionAllFilterAdmin(searchDTO.getSearchType(), searchDTO.getKeyword(),
						pageable);
			}

		} else if (searchDTO.getState() != null) {
			result = bizMoneyHistoryRepository.findAllBizMoneyAllFilterAdmin(pageable, searchDTO.getState());
		} else {
			result = bizMoneyHistoryRepository.findAllBizMoneyAdmin(pageable);
		}

		List<BizMoneyHistoryDTO> dtoList = result.getContent().stream().map(bizMoneyHistory -> {
			BizMoneyHistoryDTO dto = modelMapper.map(bizMoneyHistory, BizMoneyHistoryDTO.class);
			
			return dto;
		}).collect(Collectors.toList());

		return PageResponseDTO.<BizMoneyHistoryDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}
	
	// 3. ✨ 통계 전용 메서드 따로 만들기
	public  PageResponseDTO<Map<String, Object>> getBizMoneySummary(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("member.email").descending());
		
		Page<Object[]> result = null;
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			if (searchDTO.getState() != null) {
				result = bizMoneyHistoryRepository.searchByConditionStateFilterSummaryAdmin(searchDTO.getSearchType(), searchDTO.getKeyword(),
						searchDTO.getState(),
						pageable);
			} else {
				result = bizMoneyHistoryRepository.searchByConditionAllFilterSummaryAdmin(searchDTO.getSearchType(), searchDTO.getKeyword(),
						pageable);
			}

		} else if (searchDTO.getState() != null) {
			result = bizMoneyHistoryRepository.findAllBizMoneyAllFilterSummaryAdmin(pageable, searchDTO.getState());
		} else {
			result = bizMoneyHistoryRepository.getMemberBizMoneySummaryAdmin(pageable);
		}

		List<Map<String, Object>> dtoList = result.getContent().stream().map(row -> {
		    // row[0] = email, row[1] = type, row[2] = total (쿼리 SELECT 순서)
		    Map<String, Object> map = new HashMap<>();
		    map.put("email", row[0]);
		    map.put("type", row[1]);
		    map.put("total", row[2]);
		    return map;
		}).collect(Collectors.toList());

		// 리턴 타입도 PageResponseDTO<Map<String, Object>> 로 맞춰주세요!
		return PageResponseDTO.<Map<String, Object>>withAll()
		        .dtoList(dtoList)
		        .pageRequestDTO(searchDTO)
		        .totalCount(result.getTotalElements())
		        .build();
	}
	

}
