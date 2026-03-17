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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.honey.domain.BusinessBoard;
import com.honey.domain.Member;
import com.honey.dto.BusinessBoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BusinessBoardRepository;
import com.honey.repository.MemberRepository;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BusinessBoardServiceImpl implements BusinessBoardService {
	
	private final ModelMapper modelMapper;
	private final BusinessBoardRepository boardRepository;
	private final MemberRepository memberRepository;
	private final CustomFileUtil fileUtil;
	
	@Override
	public Long register(BusinessBoardDTO businessBoardDTO) {
		Member writer = memberRepository.findById(businessBoardDTO.getMemberEmail())
	            .orElseThrow(() -> new RuntimeException("작성자 정보를 찾을 수 없습니다."));
		
		BusinessBoard businessBoard = modelMapper.map(businessBoardDTO, BusinessBoard.class);
		
		businessBoard.changeEnabled(1);
		businessBoard.changeSign('N');
		businessBoard.setWriter(writer);
		businessBoard.setEndDate(businessBoardDTO.getEndDate());
		
		List<String> newFileNames = businessBoardDTO.getUploadFileNames();
	    if (newFileNames != null && !newFileNames.isEmpty()) {
	        newFileNames.forEach(fileName -> {
	        	businessBoard.addImageString(fileName);
	        });
	    }
		
		return boardRepository.save(businessBoard).getNo();
	}

	@Override
	@Transactional(readOnly = true)
	public BusinessBoardDTO get(Long no) {
		Optional<BusinessBoard> result = boardRepository.findById(no);
		BusinessBoard businessBoard = result.orElseThrow();
		
		BusinessBoardDTO businessBoardDTO = modelMapper.map(businessBoard, BusinessBoardDTO.class);
		
		businessBoardDTO.setWriter(businessBoard.getWriter().getNickname());
		
		List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
				.collect(Collectors.toList());

		if (fileNameList != null && !fileNameList.isEmpty()) {
			businessBoardDTO.setUploadFileNames(fileNameList);
		}
		
		return businessBoardDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponseDTO<BusinessBoardDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("no").descending());
		
		Page<BusinessBoard> result = null; 
		if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			result = boardRepository.searchByCondition(
					searchDTO.getSearchType(),
					searchDTO.getKeyword(),
					pageable);
		} else {
			result = boardRepository.findAll(pageable);
		}
		
		List<BusinessBoardDTO> dtoList = result.getContent().stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);
			
			// 작성자 닉네임 세팅 (이걸 안 하면 리스트에 이메일만 나오거나 null이 나옵니다)
		    if (businessBoard.getWriter() != null) {
		        dto.setWriter(businessBoard.getWriter().getNickname());
		    }

	        List<String> fileNameList = businessBoard.getBItemList().stream()
	                .map(item -> item.getFileName())
	                .collect(Collectors.toList());

	        if (fileNameList != null && !fileNameList.isEmpty()) {
	            dto.setUploadFileNames(fileNameList);
	        }
		
		return dto;
		}).collect(Collectors.toList());
		
		long totalCount = result.getTotalElements();
		
		PageResponseDTO<BusinessBoardDTO> responseDTO = PageResponseDTO.<BusinessBoardDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(searchDTO).totalCount(totalCount).build();
		
		return responseDTO;
	}

	@Override
	public void approve(Long no) {
		Optional<BusinessBoard> result = boardRepository.findById(no);
		BusinessBoard businessBoard = result.orElseThrow();
		
		businessBoard.changeSign('Y');
		
		boardRepository.save(businessBoard);
	}

	@Override
	public void modify(BusinessBoardDTO businessBoardDTO, BusinessBoardDTO oldBusinessBoardDTO) {
		
		List<String> oldFileNames = oldBusinessBoardDTO.getUploadFileNames();
		
		List<MultipartFile> files = businessBoardDTO.getFiles();
		
		List<String> currentUpdateFileNames = null;
		if(files != null && !files.get(0).isEmpty()) {
			currentUpdateFileNames = fileUtil.saveFiles(files);
		}
		
		List<String> uploadFileNames = businessBoardDTO.getUploadFileNames();
		
		if(currentUpdateFileNames != null && !currentUpdateFileNames.isEmpty()) {
			uploadFileNames.addAll(currentUpdateFileNames);
		}
		
		businessBoardDTO.setUploadFileNames(uploadFileNames);
		
		BusinessBoard businessBoard = boardRepository.findById(businessBoardDTO.getNo()).orElseThrow();
		
		businessBoard.clearList();
		
		List<String> newFileNames = businessBoardDTO.getUploadFileNames();
		if(newFileNames != null && !newFileNames.isEmpty()) {
			newFileNames.forEach(fileName -> {
				businessBoard.addImageString(fileName);
			});
		}
		
		businessBoard.changeTitle(businessBoardDTO.getTitle());
		businessBoard.changePrice(businessBoardDTO.getPrice());
		businessBoard.changeContent(businessBoardDTO.getContent());
		businessBoard.changeCategory(businessBoardDTO.getCategory());
		businessBoard.setEndDate(businessBoardDTO.getEndDate());
		
		
		boardRepository.save(businessBoard);
		
		if(oldFileNames != null && !oldFileNames.isEmpty()) {
			List<String> removeFiles = oldFileNames.stream().filter(fileName ->
			uploadFileNames.indexOf(fileName) == -1).collect(Collectors.toList());
			fileUtil.deleteFiles(removeFiles);
		}
	}

	@Override
	public void remove(Long no) {
		BusinessBoard businessBoard = boardRepository.findById(no).orElseThrow();
		
		businessBoard.changeEnabled(0);
		
		List<String> oldFileNames = businessBoard.getBItemList().stream().map(item -> item.getFileName()).collect(Collectors.toList());
		
		if(oldFileNames != null && !oldFileNames.isEmpty()) {
			fileUtil.deleteFiles(oldFileNames);
		}
		
		businessBoard.clearList();
		
		boardRepository.save(businessBoard);
	}
		
		
		
}
