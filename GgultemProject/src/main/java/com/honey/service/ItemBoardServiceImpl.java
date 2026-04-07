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
import com.honey.dto.ItemBoardSearchDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.CartRepository;
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
	private final CartRepository cartRepository;
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
				.orElseThrow(() -> new IllegalArgumentException("DB에 해당 이메일이 존재하지 않습니다: " + itemBoardDTO.getEmail()));

		// builder 부분
		ItemBoard itemBoard = ItemBoard.builder().title(itemBoardDTO.getTitle()).writer(itemBoardDTO.getWriter())
				.price(itemBoardDTO.getPrice()).content(itemBoardDTO.getContent()).category(itemBoardDTO.getCategory())
				.location(itemBoardDTO.getLocation()).itemUrl(itemBoardDTO.getItemUrl()).member(member)
				.lat(itemBoardDTO.getLat()).lng(itemBoardDTO.getLng()).pictureUrl(itemBoardDTO.getPictureUrl())
				.enabled(1).status("false").build();

		List<String> uploadFileNames = itemBoardDTO.getUploadFileNames();

		if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
			uploadFileNames.forEach(fileName -> {
				itemBoard.addImageString(fileName);
			});
		}
		return itemBoard;
	}

	// 리스트 검색
	@Override
	public PageResponseDTO<ItemBoardDTO> list(ItemBoardSearchDTO searchDTO) {

	    Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
	            Sort.by("regDate").descending());

	    // 기본값 설정 로직 (기존과 동일)
	    String searchType = (searchDTO.getSearchType() == null || searchDTO.getSearchType().isEmpty()) ? "all" : searchDTO.getSearchType();
	    String keyword = (searchDTO.getKeyword() == null) ? "" : searchDTO.getKeyword();
	    String status = (searchDTO.getStatus() == null || searchDTO.getStatus().isEmpty()) ? "all" : searchDTO.getStatus();
	    String category = (searchDTO.getCategory() == null || searchDTO.getCategory().isEmpty()) ? "all" : searchDTO.getCategory();
	    String location = (searchDTO.getLocation() == null || searchDTO.getLocation().isEmpty()) ? "all" : searchDTO.getLocation();
	    
	    // ★ 주의: 검색 조건의 email과 로그인한 사용자의 email을 구분해야 할 수 있지만, 
	    // 여기서는 searchDTO.getEmail()을 로그인한 사용자의 이메일로 간주합니다.
	    String currentUserEmail = searchDTO.getEmail(); 

	    Page<ItemBoard> result = itemBoardRepository.searchWithFilter(searchType, keyword, status, category, location, "all", pageable);

	    List<ItemBoardDTO> dtoList = result.getContent().stream().map(itemBoard -> {
	        ItemBoardDTO dto = modelMapper.map(itemBoard, ItemBoardDTO.class);

	        // 1. 이미지 처리 (기존 로직)
	        List<String> fileNameList = itemBoard.getItemList().stream().map(itemImage -> itemImage.getFileName()).collect(Collectors.toList());
	        dto.setUploadFileNames(fileNameList.isEmpty() ? List.of("default.jpg") : fileNameList);

	        // 2. ★ 별표 유지 핵심 로직 추가 ★
	        // 현재 로그인한 이메일이 있고, 장바구니 레파지토리에서 해당 아이템과 이메일로 데이터가 존재하는지 확인
	        if (currentUserEmail != null && !currentUserEmail.equals("all") && !currentUserEmail.isEmpty()) {
	            // CartRepository에 이 메서드가 있어야 합니다 (아래 2번 참고)
	            boolean isExist = cartRepository.existsByItemBoardIdAndMemberEmail(itemBoard.getId(), currentUserEmail);
	            dto.setFavorite(isExist); 
	        } else {
	            dto.setFavorite(false);
	        }

	        return dto;
	    }).collect(Collectors.toList());

	    long totalCount = result.getTotalElements();

	    return PageResponseDTO.<ItemBoardDTO>withAll()
	            .dtoList(dtoList)
	            .pageRequestDTO(searchDTO)
	            .totalCount(totalCount)
	            .build();
	}

	@Override
	public void modify(ItemBoardDTO itemBoardDTO) {
		ItemBoard itemBoard = itemBoardRepository.findById(itemBoardDTO.getId())
				.orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

		itemBoard.clearList();
		List<String> fileNames = itemBoardDTO.getUploadFileNames();
		if (fileNames != null && !fileNames.isEmpty()) {
			fileNames.forEach(name -> itemBoard.addImageString(name));
		}

		itemBoard.changeTitle(itemBoardDTO.getTitle());
		itemBoard.changePrice(itemBoardDTO.getPrice());
		itemBoard.changeContent(itemBoardDTO.getContent());
		itemBoard.changeCategory(itemBoardDTO.getCategory());
		itemBoard.changeLocation(itemBoardDTO.getLocation());
		itemBoard.changeLat(itemBoardDTO.getLat());
	    itemBoard.changeLng(itemBoardDTO.getLng());

		if (itemBoardDTO.getStatus() != null) {
			String status = itemBoardDTO.getStatus().trim();
			itemBoard.changeStatus(status);

			if ("판매완료".equals(status) || "true".equalsIgnoreCase(status)) {
		        itemBoard.changeEnabled(2);
		    } else if ("판매중".equals(status) || "false".equalsIgnoreCase(status)) {
		        itemBoard.changeEnabled(1);
		    }
		}

		itemBoardRepository.save(itemBoard);
	}

	@Override
	public void remove(Long id) {
		Optional<ItemBoard> result = itemBoardRepository.findById(id);
		ItemBoard itemBoard = result.orElseThrow();

		itemBoard.changeEnabled(0);

		itemBoardRepository.save(itemBoard);
	}

}
