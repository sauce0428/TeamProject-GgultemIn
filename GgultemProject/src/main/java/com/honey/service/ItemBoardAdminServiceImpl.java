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
import com.honey.dto.ItemBoardAdminDTO;
import com.honey.dto.ItemBoardDTO;
import com.honey.dto.ItemBoardSearchDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.ItemBoardAdminRepository;
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
public class ItemBoardAdminServiceImpl implements ItemBoardAdminService {

	private final ModelMapper modelMapper;
	private final ItemBoardAdminRepository itemBoardAdminRepository;
	private final ItemBoardRepository itemBoardRepository;
	private final MemberRepository memberRepository;
	private final CustomFileUtil fileUtil;

	@Override
	public ItemBoardAdminDTO get(Long id) {
		// 1. 데이터 조회 (Member까지 한 번에 가져오도록 fetch join된 repository 메서드면 더 좋습니다)
		ItemBoard itemBoard = itemBoardAdminRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));

		// 2. 기본 변환
		ItemBoardAdminDTO dto = modelMapper.map(itemBoard, ItemBoardAdminDTO.class);

		// 3. 연관된 Member 정보 수동 세팅 (매핑이 꼬일 수 있으므로 직접 넣어주는 게 안전합니다)
		if (itemBoard.getMember() != null) {
			Member m = itemBoard.getMember();
			dto.setEmail(m.getEmail());
			dto.setPw(m.getPw());
			dto.setPhone(m.getPhone());
		}

		// 4. 이미지 처리 (작성하신 코드 유지)
		if (itemBoard.getItemList() != null) {
			List<String> fileNameList = itemBoard.getItemList().stream().map(itemImage -> itemImage.getFileName())
					.collect(Collectors.toList());

			dto.setUploadFileNames(fileNameList.isEmpty() ? List.of("default.jpg") : fileNameList);
		}

		return dto;
	}

	@Override
	public Long register(ItemBoardDTO itemBoardDTO) {
		ItemBoard itemBoard = dtoToEntity(itemBoardDTO);
		ItemBoard result = itemBoardRepository.save(itemBoard);
		return result.getId();
	}

	private ItemBoard dtoToEntity(ItemBoardDTO itemBoardDTO) {
		Member member = memberRepository.findById(itemBoardDTO.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 이메일입니다: " + itemBoardDTO.getEmail()));

		ItemBoard itemBoard = ItemBoard.builder().title(itemBoardDTO.getTitle()).writer(itemBoardDTO.getWriter())
				.price(itemBoardDTO.getPrice()).content(itemBoardDTO.getContent()).category(itemBoardDTO.getCategory())
				.location(itemBoardDTO.getLocation()).itemUrl(itemBoardDTO.getItemUrl()).member(member)
				.pictureUrl(itemBoardDTO.getPictureUrl()).enabled(1).status("판매중").build();
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
	public PageResponseDTO<ItemBoardAdminDTO> list(ItemBoardSearchDTO searchDTO) {

		// 1. 페이지네이션 설정
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(), Sort.by("id").descending());

		Page<ItemBoard> result;

		// 2. 검색 조건에 따른 데이터 조회
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			result = itemBoardAdminRepository.searchByCondition(searchDTO.getSearchType(), searchDTO.getKeyword(), searchDTO.getEnabled(),
					pageable);
		} else {
			result = itemBoardAdminRepository.findAllList(searchDTO.getEnabled(), pageable);
		}

		// 3. DTO 변환 (중요: Member 정보 수동 매핑)
		List<ItemBoardAdminDTO> dtoList = result.getContent().stream().map(itemBoard -> {
			// 기본 매핑
			ItemBoardAdminDTO dto = modelMapper.map(itemBoard, ItemBoardAdminDTO.class);

			// Member 정보 수동 세팅 (화면에서 item.member.nickname 등을 쓸 수 있게)
			if (itemBoard.getMember() != null) {
				// DTO에 Member 객체 자체가 있다면 그대로 세팅
				dto.setNickname(itemBoard.getMember().getNickname());

				// 혹은 DTO에 nickname 필드가 따로 있다면 아래처럼 세팅
				// dto.setNickname(itemBoard.getMember().getNickname());
			}

			return dto;
		}).collect(Collectors.toList());

		long totalCount = result.getTotalElements();

		return PageResponseDTO.<ItemBoardAdminDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(totalCount).build();
	}

	@Override
	public void remove(Long id) {
		ItemBoard itemBoard = itemBoardAdminRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("삭제할 상품을 찾을 수 없습니다."));
		itemBoard.changeEnabled(0);
	}

	@Override
	public void soldOut(Long id) {
		ItemBoard itemBoard = itemBoardAdminRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("판매 완료 처리할 상품을 찾을 수 없습니다."));
		itemBoard.changeEnabled(2);
	}

}