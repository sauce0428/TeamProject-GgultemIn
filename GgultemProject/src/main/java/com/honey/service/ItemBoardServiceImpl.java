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
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
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
				.orElseThrow(() -> new IllegalArgumentException("DB에 해당 이메일이 존재하지 않습니다: " + itemBoardDTO.getEmail()));

		// builder 부분
		ItemBoard itemBoard = ItemBoard.builder().title(itemBoardDTO.getTitle()).writer(itemBoardDTO.getWriter())
				.price(itemBoardDTO.getPrice()).content(itemBoardDTO.getContent()).category(itemBoardDTO.getCategory())
				.location(itemBoardDTO.getLocation()).itemUrl(itemBoardDTO.getItemUrl()).member(member)
				.pictureUrl(itemBoardDTO.getPictureUrl()).enabled(1).status("false").build();

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

		// 1. 페이징 설정 (페이지 번호는 0부터 시작하므로 -1 처리)
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("regDate").descending());

		// 2. 파라미터 방어 코드 (프론트에서 null이 넘어올 경우를 대비한 기본값 설정)
		String searchType = (searchDTO.getSearchType() == null || searchDTO.getSearchType().isEmpty()) ? "all"
				: searchDTO.getSearchType();
		String keyword = (searchDTO.getKeyword() == null) ? "" : searchDTO.getKeyword();
		String status = (searchDTO.getStatus() == null || searchDTO.getStatus().isEmpty()) ? "all"
				: searchDTO.getStatus();
		String category = (searchDTO.getCategory() == null || searchDTO.getCategory().isEmpty()) ? "all"
				: searchDTO.getCategory();
		String location = (searchDTO.getLocation() == null || searchDTO.getLocation().isEmpty()) ? "all"
				: searchDTO.getLocation();
		String email = (searchDTO.getEmail() == null || searchDTO.getEmail().isEmpty()) ? "all"
				: searchDTO.getEmail();

		// 3. 레포지토리 호출 (수정된 Repository의 searchWithFilter 쿼리 사용)
		Page<ItemBoard> result = itemBoardRepository.searchWithFilter(searchType, keyword, status, category, location,
				email,pageable);

		// 4. 엔티티(Entity) 리스트를 DTO 리스트로 변환
		List<ItemBoardDTO> dtoList = result.getContent().stream().map(itemBoard -> {

			// ModelMapper를 이용한 기본 필드 복사
			ItemBoardDTO dto = modelMapper.map(itemBoard, ItemBoardDTO.class);

			// 이미지 파일 리스트 처리
			List<String> fileNameList = itemBoard.getItemList().stream().map(itemImage -> itemImage.getFileName())
					.collect(Collectors.toList());

			// 이미지가 없을 경우 디폴트 이미지 설정
			if (fileNameList == null || fileNameList.isEmpty()) {
				dto.setUploadFileNames(List.of("default.jpg"));
			} else {
				dto.setUploadFileNames(fileNameList);
			}

			return dto;

		}).collect(Collectors.toList());

		// 5. 전체 데이터 개수 추출
		long totalCount = result.getTotalElements();

		// 6. PageResponseDTO 구성하여 반환
		return PageResponseDTO.<ItemBoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO).totalCount(totalCount)
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

		if (itemBoardDTO.getStatus() != null) {
			String status = itemBoardDTO.getStatus().trim();
			itemBoard.changeStatus(status);

			if ("판매완료".equals(status)) {
				itemBoard.changeEnabled(2);
			} else if ("판매중".equals(status)) {
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
