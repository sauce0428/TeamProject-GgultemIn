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

import com.honey.domain.BlockList;
import com.honey.dto.BlockListDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BlockListRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BlockListServiceImpl implements BlockListService {
	private final ModelMapper modelMapper;
	private final BlockListRepository repository;

	@Override
	public BlockListDTO get(Long no) {
		Optional<BlockList> result = repository.findById(no);
		BlockList blockList = result.orElseThrow();
		
		BlockListDTO blockListDTO = modelMapper.map(blockList, BlockListDTO.class);
		
		return blockListDTO;
	}
	
	@Override
	public Long register(BlockListDTO blockListDTO) {
	    // ModelMapper 대신 Builder 패턴을 사용하여 명확하게 객체를 생성합니다.
	    // 이렇게 하면 매핑 충돌 에러가 발생하지 않습니다.
	    BlockList blockList = BlockList.builder()
	            .memberEmail(blockListDTO.getMemberEmail())
	            .blockId(blockListDTO.getBlockId())
	            .reason(blockListDTO.getReason())
	            .enabled(1) // 활성화 상태로 설정
	            .build();
	    
	    log.info("등록될 블랙리스트 엔티티: " + blockList);
	    
	    return repository.save(blockList).getNo();
	}

	@Override
	public PageResponseDTO<BlockListDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("no").descending());
		Page<BlockList> result = repository.findAllByEnabled(pageable);
		
		List<BlockListDTO> dtoList = result.getContent().stream().map(blockList -> {
			BlockListDTO dto = modelMapper.map(blockList, BlockListDTO.class);
	        return dto;
	    }).collect(Collectors.toList());

	long totalCount = result.getTotalElements();

	PageResponseDTO<BlockListDTO> responseDTO = PageResponseDTO.<BlockListDTO>withAll().dtoList(dtoList)
			.pageRequestDTO(searchDTO).totalCount(totalCount).build();

	return responseDTO;
	}
	
	@Override
	public void modify(BlockListDTO blockListDTO) {
		Optional<BlockList> result = repository.findById(blockListDTO.getNo());
		BlockList blockList = result.orElseThrow();

		blockList.changeReason(blockListDTO.getReason());

	    repository.save(blockList);
	}
	
	@Override
	public void remove(Long no) {
		Optional<BlockList> result = repository.findById(no);
		BlockList blockList = result.orElseThrow();
		
		blockList.changeEnabled(0);

		repository.save(blockList);
	}
	
}
