package com.honey.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.ChatMessages;
import com.honey.domain.ChatRoom;
import com.honey.dto.ChatRoomDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.ChatMessageRepository;
import com.honey.repository.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
	private final ModelMapper modelMapper;
	private final ChatRoomRepository repository;
	private final ChatMessageRepository chatMessageRepository;

	@Override
	public ChatRoomDTO get(Long roomId) {
		Optional<ChatRoom> result = repository.findById(roomId);
		ChatRoom chatRoom = result.orElseThrow();
		
		ChatRoomDTO chatRoomDTO = modelMapper.map(chatRoom, ChatRoomDTO.class);
		
		return chatRoomDTO;
	}
	
	@Override
	public Long register(ChatRoomDTO chatRoomDTO) {
		// 1. 이미 존재하는 방인지 확인 (구매자, 판매자, 상품번호 조합)
	    Optional<ChatRoom> existingRoom = repository.findByBuyerIdAndSellerIdAndItemId(
	        chatRoomDTO.getBuyerId(), 
	        chatRoomDTO.getSellerId(), 
	        chatRoomDTO.getItemId()
	    );

	    if (existingRoom.isPresent()) {
	        log.info("기존 채팅방으로 연결합니다. ID: {}", existingRoom.get().getRoomId());
	        return existingRoom.get().getRoomId();
	    }

	    // 2. 없는 경우에만 새로 생성
	    ChatRoom chatRoom = modelMapper.map(chatRoomDTO, ChatRoom.class);
	    chatRoom.changeEnabled(1);
	    return repository.save(chatRoom).getRoomId();
	}

	@Override
	public PageResponseDTO<ChatRoomDTO> list(SearchDTO searchDTO) {
	    String userId = searchDTO.getKeyword();
	    
	    // ✅ 여기서 단순히 findAll 하는 게 아니라, 내 방만 골라오는 로직 실행!
	    List<ChatRoom> rooms = repository.findActiveRoomsByUserId(userId);

	    List<ChatRoomDTO> dtoList = rooms.stream().map(room -> {
	        Optional<ChatMessages> lastMsgOpt = chatMessageRepository.findTopByChatRoomOrderByRegDateDesc(room);
	        Long unread = chatMessageRepository.countUnreadMessages(room.getRoomId(), userId);

	        ChatRoomDTO dto = modelMapper.map(room, ChatRoomDTO.class);
	        dto.setLastMessage(lastMsgOpt.map(ChatMessages::getContent).orElse("대화 내용이 없습니다."));
	        dto.setLastSendTime(lastMsgOpt.map(ChatMessages::getRegDate).orElse(room.getRegDate()));
	        dto.setUnReadCount(unread);
	        return dto;
	    })
	    .sorted(Comparator.comparing(ChatRoomDTO::getLastSendTime).reversed()) // 최신순 🍯
	    .collect(Collectors.toList());

	    return PageResponseDTO.<ChatRoomDTO>withAll()
	            .dtoList(dtoList)
	            .pageRequestDTO(searchDTO)
	            .totalCount((long) dtoList.size())
	            .build();
	}
	
	@Override
	public void modify(ChatRoomDTO chatRoomDTO) {
		Optional<ChatRoom> result = repository.findById(chatRoomDTO.getRoomId());
		ChatRoom chatRoom = result.orElseThrow();

		chatRoom.changeRoomName(chatRoomDTO.getRoomName());

	    repository.save(chatRoom);
	}
	
	@Override
	public void remove(Long roomId) {
		Optional<ChatRoom> result = repository.findById(roomId);
		ChatRoom chatRoom = result.orElseThrow();
		
		chatRoom.changeEnabled(0);

		repository.save(chatRoom);
	}
	
	public List<ChatRoomDTO> getMyChatRooms(String userId) {
        // 1. 내가 판매자거나 구매자인 활성화된 채팅방 목록 조회
        List<ChatRoom> rooms = repository.findActiveRoomsByUserId(userId);

        return rooms.stream().map(room -> {
            // 2. 마지막 메시지 조회
            Optional<ChatMessages> lastMsgOpt = chatMessageRepository.findTopByChatRoomOrderByRegDateDesc(room);
            
            // 3. 안 읽은 메시지 개수 조회
            Long unread = chatMessageRepository.countUnreadMessages(room.getRoomId(), userId);

            // 4. DTO 변환
            return ChatRoomDTO.builder()
                    .roomId(room.getRoomId())
                    .itemId(room.getItemId())
                    .sellerId(room.getSellerId())
                    .buyerId(room.getBuyerId())
                    .roomName(room.getRoomName())
                    .lastMessage(lastMsgOpt.map(ChatMessages::getContent).orElse("대화 내용이 없습니다."))
                    .lastSendTime(lastMsgOpt.map(ChatMessages::getRegDate).orElse(room.getRegDate()))
                    .unReadCount(unread)
                    .build();
        })
        .sorted(Comparator.comparing(ChatRoomDTO::getLastSendTime).reversed()) // 5. ✨ 최신순 정렬!
        .collect(Collectors.toList());
    }

	@Override
	@Transactional // ✅ 데이터 수정을 위해 반드시 필요합니다!
	public void markAsRead(Long roomId, String userId) {
	    // 1. 해당 방에서 '나(userId)'가 아닌 상대방이 보낸 메시지들을 찾아 '읽음(1)' 처리
	    int updatedCount = chatMessageRepository.updateReadStatus(roomId, userId);
	    
	    // 2. 잘 처리됐는지 로그로 확인 (선택 사항) 🍯
	    log.info("방 번호 [{}]에서 {}님의 안 읽은 메시지 {}개를 읽음 처리했습니다. 🐝", roomId, userId, updatedCount);
	}

	
}
