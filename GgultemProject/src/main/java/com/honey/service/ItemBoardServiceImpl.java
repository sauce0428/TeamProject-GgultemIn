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
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.ItemBoardRepository;
import com.honey.repository.MemberRepository;
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
	private final MemberRepository memberRepository;
	private final CustomFileUtil fileUtil;

	@Override
	public ItemBoardDTO get(Long id) {
		Optional<ItemBoard> result = itemBoardRepository.findById(id);
		ItemBoard itemBoard = result.orElseThrow();

		itemBoard.chanceViewCount(itemBoard.getViewCount() + 1);

		itemBoardRepository.save(itemBoard);

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
		ItemBoard itemBoard = dtoToEntity(itemBoardDTO);
		ItemBoard result = itemBoardRepository.save(itemBoard);
		return result.getId();
	}

	private ItemBoard dtoToEntity(ItemBoardDTO itemBoardDTO) {
		Member member = memberRepository.findById(itemBoardDTO.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("DB에 없는 이메일입니다: " + itemBoardDTO.getEmail()));

		ItemBoard itemBoard = ItemBoard.builder().title(itemBoardDTO.getTitle()).writer(itemBoardDTO.getWriter())
				.price(itemBoardDTO.getPrice()).content(itemBoardDTO.getContent()).category(itemBoardDTO.getCategory())
				.location(itemBoardDTO.getLocation()).itemUrl(itemBoardDTO.getItemUrl()).member(member)
				.pictureUrl(itemBoardDTO.getPictureUrl()).enabled(0).status("판매중").build();
		// 업로드 처리가 끝난 파일들의 이름 리스트
		List<String> uploadFileNames = itemBoardDTO.getUploadFileNames();
		if (uploadFileNames == null) {
			return itemBoard;
		}
		uploadFileNames.stream().forEach(uploadName -> {
			itemBoard.addImageString(uploadName);
		});

		return itemBoard;
	}

	@Override
	public PageResponseDTO<ItemBoardDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(), Sort.by("id").descending());
		Page<ItemBoard> result = null;

		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			result = itemBoardRepository.searchByCondition(searchDTO.getSearchType(), searchDTO.getKeyword(), pageable);
		} else {
			result = itemBoardRepository.findAllList(pageable);
		}

		List<ItemBoardDTO> dtoList = result.getContent().stream().map(itemBoard -> {
			ItemBoardDTO dto = modelMapper.map(itemBoard, ItemBoardDTO.class);

			List<String> fileNameList = itemBoard.getItemList().stream().map(itemImage -> itemImage.getFileName())
					.collect(Collectors.toList());

			dto.setUploadFileNames(fileNameList);

			return dto;
		}).collect(Collectors.toList());
		long totalCount = result.getTotalElements();
		PageResponseDTO<ItemBoardDTO> responseDTO = PageResponseDTO.<ItemBoardDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(searchDTO).totalCount(totalCount).build();

		return responseDTO;
	}

	@Override
	public void modify(ItemBoardDTO itemBoardDTO) {
		Optional<ItemBoard> result = itemBoardRepository.findById(itemBoardDTO.getId());
		ItemBoard itemBoard = result.orElseThrow();

		itemBoard.clearList();

		List<String> fileNames = itemBoardDTO.getUploadFileNames();

		// 3. 다시 하나씩 추가 (이 과정이 없으면 DB에서 삭제됩니다)
		if (fileNames != null && !fileNames.isEmpty()) {
			fileNames.forEach(name -> {
				itemBoard.addImageString(name);
			});
		}

		itemBoard.changeTitle(itemBoardDTO.getTitle());
		itemBoard.changePrice(itemBoardDTO.getPrice());
		itemBoard.changeContent(itemBoardDTO.getContent());
		itemBoard.changeCategory(itemBoardDTO.getCategory());
		itemBoard.changeLocation(itemBoardDTO.getLocation());

		// 판매 상태(판매중, 판매완료)
		if (itemBoardDTO.getStatus() != null && !itemBoardDTO.getStatus().isEmpty()) {
			itemBoard.changeStatus(itemBoardDTO.getStatus());
		}

		List<String> uploadFileNames = itemBoardDTO.getUploadFileNames();
		if (uploadFileNames != null && uploadFileNames.isEmpty()) {
			uploadFileNames.stream().forEach(uploadName -> {
				itemBoard.addImageString(uploadName);
			});
		}
		itemBoardRepository.save(itemBoard);
	}

	@Override
	public void remove(Long id) {
		Optional<ItemBoard> result = itemBoardRepository.findById(id);
		ItemBoard itemBoard = result.orElseThrow();

		itemBoard.changeEnabled(1);

		itemBoardRepository.save(itemBoard);
	}

}
