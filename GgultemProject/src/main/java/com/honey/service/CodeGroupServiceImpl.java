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

import com.honey.domain.CodeGroup;
import com.honey.dto.CodeGroupDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.CodeGroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CodeGroupServiceImpl implements CodeGroupService {
	private final ModelMapper modelMapper;
	private final CodeGroupRepository repository;

	@Override
	public CodeGroupDTO get(Long groupCode) {
		Optional<CodeGroup> result = repository.findById(groupCode);
		CodeGroup codeGroup = result.orElseThrow();
		
		CodeGroupDTO codeGroupDTO = modelMapper.map(codeGroup, CodeGroupDTO.class);
		
		return codeGroupDTO;
	}
	
	@Override
	public Long register(CodeGroupDTO codeGroupDTO) {
	    // ModelMapper 대신 Builder 패턴을 사용하여 명확하게 객체를 생성합니다.
	    // 이렇게 하면 매핑 충돌 에러가 발생하지 않습니다.
		CodeGroup codeGroup = CodeGroup.builder()
	            .groupCode(codeGroupDTO.getGroupCode())
	            .groupName(codeGroupDTO.getGroupName())
	            .useYn(codeGroupDTO.getUseYn())
	            .enabled(1) // 활성화 상태로 설정
	            .build();
	    
	    log.info("" + codeGroup);
	    
	    return repository.save(codeGroup).getGroupCode();
	}

	@Override
	public PageResponseDTO<CodeGroupDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("groupCode").descending());
		Page<CodeGroup> result = repository.findAllByEnabled(pageable);
		
		List<CodeGroupDTO> dtoList = result.getContent().stream().map(codeGroup -> {
			CodeGroupDTO dto = modelMapper.map(codeGroup, CodeGroupDTO.class);
	        return dto;
	    }).collect(Collectors.toList());

	long totalCount = result.getTotalElements();

	PageResponseDTO<CodeGroupDTO> responseDTO = PageResponseDTO.<CodeGroupDTO>withAll().dtoList(dtoList)
			.pageRequestDTO(searchDTO).totalCount(totalCount).build();

	return responseDTO;
	}
	
	@Override
	public void modify(CodeGroupDTO codeGroupDTO) {
		Optional<CodeGroup> result = repository.findById(codeGroupDTO.getGroupCode());
		CodeGroup codeGroup = result.orElseThrow();

		codeGroup.changeGroupName(codeGroupDTO.getGroupName());

	    repository.save(codeGroup);
	}
	
	@Override
	public void remove(Long groupCode) {
		Optional<CodeGroup> result = repository.findById(groupCode);
		CodeGroup codeGroup = result.orElseThrow();
		
		codeGroup.changeEnabled(0);

		repository.save(codeGroup);
	}
	
}
