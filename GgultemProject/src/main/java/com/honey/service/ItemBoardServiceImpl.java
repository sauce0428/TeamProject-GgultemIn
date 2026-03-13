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

import com.honey.domain.ItemBoard;
import com.honey.domain.Member;
import com.honey.dto.ItemBoardDTO;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.ItemBoardRepository;
import com.honey.util.CustomFileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemBoardServiceImpl implements ItemBoardService {

	private final ModelMapper modelMapper;
	private final ItemBoardRepository itemBoardRepository;
	private final CustomFileUtil fileUtil;

	@Override
	public ItemBoardDTO get(Long id) {
		Optional<ItemBoard> result = itemBoardRepository.findById(id);
		ItemBoard itemBoard = result.orElseThrow();

		ItemBoardDTO itemBoardDTO = modelMapper.map(itemBoard, ItemBoardDTO.class);

		List<String> fileNameList = itemBoard.getItemList().stream().map(itemList -> itemList.getFileName())
				.collect(Collectors.toList());

		if (fileNameList != null && !fileNameList.isEmpty()) {
			itemBoardDTO.setUploadFileNames(fileNameList);
		} else {
			itemBoardDTO.setUploadFileNames(List.of("default.jpg"));
		}

		return itemBoardDTO;
	}

	@Override
	public Long register(ItemBoardDTO itemBoardDTO) {
		// 1. 매핑 전 로그 확인
	    log.info("---------------------------------------");
	    log.info("서비스 시작 - DTO 파일명 리스트: " + itemBoardDTO.getUploadFileNames());
		ItemBoard itemBoard = modelMapper.map(itemBoardDTO, ItemBoard.class);
		// 파일명 리스트 가져오기
		log.info("매핑 후 엔티티 상태 - itemList null 여부: " + (itemBoard.getItemList() == null));
		List<String> uploadFileNames = itemBoardDTO.getUploadFileNames();
		if (itemBoardDTO.getUploadFileNames() != null && !itemBoardDTO.getUploadFileNames().isEmpty()) {
			log.info("파일명 발견! 등록을 진행합니다: " + uploadFileNames);
			itemBoardDTO.getUploadFileNames().forEach(fileName -> {
				itemBoard.addImageString(fileName);
			});
		} else {
			log.error("파일명 리스트가 비어있습니다. 그래서 default.jpg가 들어갑니다.");
			itemBoard.addImageString("default.jpg");
		}
		ItemBoard savedItemBoard = itemBoardRepository.save(itemBoard);

		return savedItemBoard.getId();
	}

	@Override
	public PageResponseDTO<ItemBoardDTO> list(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, pageRequestDTO.getSize(),
				Sort.by("id").descending());
		Page<ItemBoard> result = itemBoardRepository.findAll(pageable);
		List<ItemBoardDTO> dtoList = result.getContent().stream()
				.map(itemBoard -> modelMapper.map(itemBoard, ItemBoardDTO.class)).collect(Collectors.toList());
		long totalCount = result.getTotalElements();
		PageResponseDTO<ItemBoardDTO> responseDTO = PageResponseDTO.<ItemBoardDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

		return responseDTO;
	}

	@Override
	public void modify(ItemBoardDTO itemBoardDTO) {
		Optional<ItemBoard> result = itemBoardRepository.findById(itemBoardDTO.getId());
		ItemBoard itemBoard = result.orElseThrow();

		itemBoard.changeTitle(itemBoardDTO.getTitle());
		itemBoard.changeWriter(itemBoardDTO.getWriter());
		itemBoard.changePrice(itemBoardDTO.getPrice());
		itemBoard.changeContent(itemBoardDTO.getContent());
		itemBoard.changeCategory(itemBoardDTO.getCategory());
		itemBoard.changeStatus(itemBoardDTO.getStatus());
		itemBoard.changeLocation(itemBoardDTO.getLocation());

		itemBoardRepository.save(itemBoard);
	}

	@Override
	public void remove(Long id) {
		Optional<ItemBoard> result = itemBoardRepository.findById(id);
		ItemBoard itemBoard = result.orElseThrow();

		itemBoardRepository.save(itemBoard);
	}

}
