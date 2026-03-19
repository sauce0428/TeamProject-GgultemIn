package com.honey.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.CodeDetail;
import com.honey.dto.CodeDetailDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.CodeDetailRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CodeDetailServiceImpl implements CodeDetailService {
	private final ModelMapper modelMapper;
	private final CodeDetailRepository repository;

	@Override
	public CodeDetailDTO get(String groupCode) {
		Optional<CodeDetail> result = repository.findById(groupCode);
		CodeDetail codeDetail = result.orElseThrow();
		
		CodeDetailDTO codeDetailDTO = modelMapper.map(codeDetail, CodeDetailDTO.class);
		
		return codeDetailDTO;
	}
	
	@Override
	public String register(CodeDetailDTO codeDetailDTO) {
	    log.info("--- Register DTO: " + codeDetailDTO);

	    CodeDetail codeDetail = CodeDetail.builder()
	            // [수정 포인트] String PK이므로 사용자가 보낸 값을 직접 세팅해야 합니다!
	            .groupCode(codeDetailDTO.getGroupCode()) 
	            .codeValue(codeDetailDTO.getCodeValue())
	            .codeName(codeDetailDTO.getCodeName())
	            .sortSeq(codeDetailDTO.getSortSeq())
	            .useYn(codeDetailDTO.getUseYn())
	            .enabled(1)
	            .build();
	    
	    // 저장 전 로그에서 groupCode가 null인지 꼭 확인하세요.
	    log.info("--- 저장 직전 엔티티: " + codeDetail);
	    
	    CodeDetail saved = repository.save(codeDetail);
	    
	    return saved.getGroupCode();
	}

	@Override
	public PageResponseDTO<CodeDetailDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("groupCode").descending());
		Page<CodeDetail> result = repository.findAllByEnabled(pageable);
		
		List<CodeDetailDTO> dtoList = result.getContent().stream().map(codeDetail -> {
			CodeDetailDTO dto = modelMapper.map(codeDetail, CodeDetailDTO.class);
	        return dto;
	    }).collect(Collectors.toList());

	long totalCount = result.getTotalElements();

	PageResponseDTO<CodeDetailDTO> responseDTO = PageResponseDTO.<CodeDetailDTO>withAll().dtoList(dtoList)
			.pageRequestDTO(searchDTO).totalCount(totalCount).build();

	return responseDTO;
	}
	
	@Override
	public void modify(CodeDetailDTO codeDetailDTO) {
		Optional<CodeDetail> result = repository.findById(codeDetailDTO.getGroupCode());
		CodeDetail codeDetail = result.orElseThrow();

		codeDetail.changeCodeName(codeDetailDTO.getCodeName());

	    repository.save(codeDetail);
	}
	
	@Override
	public void remove(String groupCode) {
		Optional<CodeDetail> result = repository.findById(groupCode);
		CodeDetail codeDetail = result.orElseThrow();
		
		codeDetail.changeEnabled(0);

		repository.save(codeDetail);
	}
	
}
