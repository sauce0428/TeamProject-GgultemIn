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

import com.honey.domain.Cart;
import com.honey.domain.ItemBoard;
import com.honey.domain.Member;
import com.honey.dto.CartDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.CartRepository;
import com.honey.repository.ItemBoardRepository;
import com.honey.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final ModelMapper modelMapper;
	private final MemberRepository memberRepository;
	private final CartRepository cartRepository;
	private final ItemBoardRepository itemBoardRepository;
	
	@Override
	public CartDTO get(Long id) {
		Cart cart = cartRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("해당 장바구니 아이템이 없습니다."));
		
		ItemBoard itemBoard = cart.getItemBoard();
		
		return CartDTO.builder().id(cart.getId()).itemId(itemBoard.getId())
		.member(cart.getMember())
		.itemBoard(itemBoard)
		.build();
	}

	@Override
	public Long register(CartDTO cartDTO) {
		ItemBoard itemBoard = itemBoardRepository.findById(cartDTO.getItemId())
				.orElseThrow(()-> new RuntimeException("등록된 상품이 없습니다."));
		
		Member member = memberRepository.findById(cartDTO.getEmail())
				.orElseThrow(()->new RuntimeException("회원이 없습니다."));
		
		Cart cart = Cart.builder()
				.itemBoard(itemBoard)
				.member(member)
				.enabled(0)
				.build();
		Cart result = cartRepository.save(cart);
		return result.getId();
	}

	@Override
	public PageResponseDTO<CartDTO> list(SearchDTO searchDTO, Long memberEmail) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() -1, searchDTO.getSize(),
				Sort.by("id").descending());
		Page<Cart> result = null;
		
		if(searchDTO != null && !searchDTO.getKeyword().isEmpty()) {
			result = cartRepository.searchByCondition(
					searchDTO.getSearchType(),
					searchDTO.getKeyword(),
					pageable, memberEmail);
		}else {
			result = cartRepository.findAll(pageable);
		}
		
		List<CartDTO> dtoList = result.getContent().stream()
				.map(Cart -> modelMapper.map(Cart, CartDTO.class)).collect(Collectors.toList());
		long totalCount = result.getTotalElements();
		PageResponseDTO<CartDTO> responseDTO = PageResponseDTO.<CartDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(searchDTO).totalCount(totalCount).build();
		
		return responseDTO;
	}

	@Override
	public void remove(Long id) {
		Optional<Cart> result = cartRepository.findById(id);
		Cart cart = result.orElseThrow();
		cart.changeEnabled(1);
		cartRepository.save(cart);
	}


}
