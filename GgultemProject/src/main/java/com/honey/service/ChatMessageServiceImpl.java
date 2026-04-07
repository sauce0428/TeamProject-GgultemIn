package com.honey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.honey.domain.ChatMessages;
import com.honey.domain.ChatRoom;
import com.honey.dto.ChatMessageDTO;
import com.honey.repository.ChatMessageRepository;
import com.honey.repository.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService{
	private final ModelMapper modelMapper;
	public final ChatMessageRepository chatMessagesRepository;
	private final ChatRoomRepository chatRoomRepository;
	
	@Override
	public void saveMessage(ChatMessageDTO dto) {
	    // 1. 방 정보를 찾아서 Entity로 변환 (ChatRoom 객체가 필요함)
	    ChatRoom chatRoom = chatRoomRepository.findById(dto.getRoomId()).orElseThrow();
	    
	    ChatMessages entity = ChatMessages.builder()
	            .chatRoom(chatRoom)
	            .senderId(dto.getSenderId())
	            .content(dto.getContent())
	            .isRead(0) // 기본값 미읽음
	            .build();
	            
	    chatMessagesRepository.save(entity);
	}

	@Override
	public List<ChatMessageDTO> getMessagesByRoom(Long roomId) {
	    List<ChatMessages> entities = chatMessagesRepository.findByChatRoomRoomIdOrderByRegDateAsc(roomId);
	    
	    return entities.stream().map(entity -> {
	        // ✨ ModelMapper 대신 직접 DTO를 생성합니다. (가장 확실한 해결책!)
	        ChatMessageDTO dto = new ChatMessageDTO();
	        
	        // 1. 방 번호 추출
	        if (entity.getChatRoom() != null) {
	            dto.setRoomId(entity.getChatRoom().getRoomId());
	        }
	        
	        // 2. 나머지 필드 설정 (DTO 필드명에 맞춰서 확인!)
	        dto.setSenderId(entity.getSenderId());
	        dto.setContent(entity.getContent());
	        dto.setIsRead(entity.getIsRead());
	        
	        // 3. 시간 설정 (BaseTimeEntity의 regDate 사용)
	        if (entity.getRegDate() != null) {
	            dto.setRegDate(entity.getRegDate().toString());
	        }
	        
	        return dto;
	    }).collect(Collectors.toList());
	}
	
	@Transactional
	public void markAsRead(Long roomId, String userId) {
	    // 상대방이 보낸 메시지 중 이 방의 안 읽은 메시지를 모두 읽음(1) 처리
	    // UPDATE chatmessages SET isRead = 1 WHERE roomId = :roomId AND senderId != :userId
		chatMessagesRepository.updateReadStatus(roomId, userId);
	}
}
