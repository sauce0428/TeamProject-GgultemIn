package com.honey.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.honey.dto.ChatMessageDTO;
import com.honey.service.ChatMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//스프링 MVC의 컨트롤러 (웹소켓 메시지도 처리 가능)
@Controller
@RequiredArgsConstructor
public class ChatController {

	// ✨ 특정 목적지로 메시지를 직접 보내기 위한 템플릿
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessagesService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessageDTO chatMessageDTO) {
        
        // 1. DTO에 방 번호 세팅 (roomId는 String으로 들어오니 Long으로 변환)
        chatMessageDTO.setRoomId(Long.parseLong(roomId));
        
        // 2. ✨ DB에 메시지 저장 (Service 호출)
        chatMessagesService.saveMessage(chatMessageDTO); 

        // 3. 서버 시간 주입 후 전송
        chatMessageDTO.setRegDate(LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, chatMessageDTO);
    }
}