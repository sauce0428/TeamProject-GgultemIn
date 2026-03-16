package com.honey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.BusinessMember;
import com.honey.domain.SearchLog;
import com.honey.dto.BusinessMemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.dto.SearchLogDTO;
import com.honey.repository.SearchLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchLogServiceImpl implements SearchLogService {
	private final ModelMapper modelMapper;
	private final SearchLogRepository searchRepository;

	@Override
	public void logSearch(SearchDTO searchDTO) {
		if (searchDTO.getKeyword() == null || searchDTO.getKeyword().trim().isEmpty()) {
	        return;
	    }
		
	    SearchLog searchLog = SearchLog.builder()
	            .keyword(searchDTO.getKeyword().trim())
	            .searchType(searchDTO.getSearchType())
	            .build();
		
		searchRepository.save(searchLog);
	}

	@Override
	public PageResponseDTO<SearchLogDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("no").descending());
		
		Page<SearchLog> result = null;
		if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			result = searchRepository.searchByCondition(
					searchDTO.getSearchType(),
					searchDTO.getKeyword(),
					pageable);
		} else {
			result = searchRepository.findAll(pageable);
		}
		
		List<SearchLogDTO> dtoList = result.getContent().stream().map(searchLog -> {
			SearchLogDTO dto = modelMapper.map(searchLog, SearchLogDTO.class);
			return dto; // 반드시 DTO를 리턴해야 합니다!
	    }).collect(Collectors.toList());
		
			long totalCount = result.getTotalElements();
		
			PageResponseDTO<SearchLogDTO> responseDTO = PageResponseDTO.<SearchLogDTO>withAll().dtoList(dtoList)
					.pageRequestDTO(searchDTO).totalCount(totalCount).build();

			return responseDTO;
	}

	@Override
	public PageResponseDTO<SearchLogDTO> listRank(SearchDTO searchDTO) {
		// TODO Auto-generated method stub
		return null;
	}


}
