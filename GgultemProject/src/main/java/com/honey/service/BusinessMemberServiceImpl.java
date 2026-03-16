package com.honey.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.BusinessMember;
import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BusinessMemberRepository;
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
	private final CustomFileUtil fileUtil;
	
	@Override
	public BusinessMemberDTO get(Long no) {
		Optional<BusinessMember> result = bMemberRepository.findById(no);
		BusinessMember bMember = result.orElseThrow();
		Set<String> authSet = bMember.getAuthSet();
		
		BusinessMemberDTO bMemberDTO = modelMapper.map(bMember, BusinessMemberDTO.class);
		
		bMemberDTO.setAuth(authSet);
		
		return bMemberDTO;
	}

	@Override
	public PageResponseDTO<BusinessMemberDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("no").descending());
		
		Page<BusinessMember> result = null;
		if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			result = bMemberRepository.searchByCondition(
					searchDTO.getSearchType(),
					searchDTO.getKeyword(),
					pageable);
		} else {
			result = bMemberRepository.findAll(pageable);
		}
		
		List<BusinessMemberDTO> dtoList = result.getContent().stream().map(businessMember -> {
			BusinessMemberDTO dto = modelMapper.map(businessMember, BusinessMemberDTO.class);
			return dto; // 반드시 DTO를 리턴해야 합니다!
	    }).collect(Collectors.toList());
		
			long totalCount = result.getTotalElements();
		
			PageResponseDTO<BusinessMemberDTO> responseDTO = PageResponseDTO.<BusinessMemberDTO>withAll().dtoList(dtoList)
					.pageRequestDTO(searchDTO).totalCount(totalCount).build();

			return responseDTO;
	}

	@Override
	public Long register(BusinessMemberDTO businessMemberDTO) {
		BusinessMember bMember = modelMapper.map(businessMemberDTO, BusinessMember.class);
		
		bMember.builder().coin(0).enabled(0).build();
		
		bMember.addRole("ROLE_MEMBER");
		
		BusinessMember saveBMember = bMemberRepository.save(bMember);
		
		return saveBMember.getNo();
	}

	@Override
	public void approve(BusinessMemberDTO bMemberDTO) {
		Optional<BusinessMember> result = bMemberRepository.findById(bMemberDTO.getNo());
		BusinessMember bMember = result.orElseThrow();
		
		bMember.changeStatus(1);
		bMember.addRole("ROLE_BUSINESS_MEMBER");
		
		bMemberRepository.save(bMember);
	}

	@Override
	public void remove(Long no) {
		Optional<BusinessMember> result = bMemberRepository.findById(no);
		BusinessMember bMember = result.orElseThrow();
		
		bMember.changeStatus(0);
		
		bMemberRepository.save(bMember);
	}

	@Override
	public void modify(BusinessMemberDTO bMemberDTO) {
		Optional<BusinessMember> result = bMemberRepository.findById(bMemberDTO.getNo());
		BusinessMember bMember = result.orElseThrow();
		
		bMember.changePw(bMemberDTO.getPw());
		
		bMemberRepository.save(bMember);
	}
	
	

}
