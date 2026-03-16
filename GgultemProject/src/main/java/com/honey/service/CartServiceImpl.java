package com.honey.service;

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
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
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
		Long itemId = itemBoard.getId();
		
		return CartDTO.builder().id(cart.getId()).itemId(itemBoard.getId())
		.member(cart.getMember())
		.itemBoard(itemBoard)
		.build();
	}

	@Override
	public Long register(CartDTO cartDTO) {
		ItemBoard itemBoard = itemBoardRepository.findById(cartDTO.getItemId())
				.orElseThrow(()-> new RuntimeException("등록된 상품이 없습니다."));
		
		Member member = memberRepository.findById(cartDTO.getMember().getNo())
				.orElseThrow(()->new RuntimeException("회원이 없습니다."));
		
		Cart cart = Cart.builder()
				.itemBoard(itemBoard)
				.member(member)
				.build();
		Cart result = cartRepository.save(cart);
		return result.getId();
	}

	@Override
	public PageResponseDTO<CartDTO> list(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize(),
				Sort.by("id").descending());
		return null;
	}


}
