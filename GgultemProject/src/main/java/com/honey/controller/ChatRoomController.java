package com.honey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.ChatMessageDTO;
import com.honey.dto.ChatRoomDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.ChatMessageService;
import com.honey.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/chatroom")
public class ChatRoomController {

	private final ChatRoomService service;
	private final ChatMessageService chatMessagesService;
	
	@GetMapping("/{roomId}")
	public ChatRoomDTO getChatRoom(@PathVariable(name = "roomId") Long roomId) {
		return service.get(roomId);
	}
	
	@PostMapping("/")
	public Map<String, Long> register(@RequestBody ChatRoomDTO chatRoomDTO) {
		Long roomId = service.register(chatRoomDTO);
		return Map.of("roomId", roomId);
	}
	
	@GetMapping("/list")
	// 프론트에서 PageResponseDTO 구조를 기대하므로, 그 안에 우리가 만든 리스트를 담아줍니다.
	public PageResponseDTO<ChatRoomDTO> list(SearchDTO searchDTO) {
	    
	    // 1. searchDTO에서 로그인한 사용자의 이메일(keyword)을 가져옵니다.
	    String userId = searchDTO.getKeyword(); 
	    
	    // 2. 우리가 새로 만든 '개인 맞춤형 채팅방 목록' 메서드 호출! 🍯
	    List<ChatRoomDTO> dtoList = service.getMyChatRooms(userId);
	    
	    // 3. 기존 PageResponseDTO 형식에 맞춰서 반환 (프론트 코드 유지용)
	    return PageResponseDTO.<ChatRoomDTO>withAll()
	            .dtoList(dtoList)
	            .pageRequestDTO(searchDTO)
	            .totalCount((long) dtoList.size())
	            .build();
	}
	
	@PutMapping("/{roomId}")
	public Map<String, String> modify(@PathVariable(name = "roomId") Long roomId, @RequestBody ChatRoomDTO chatRoomDTO) {
		chatRoomDTO.setRoomId(roomId);
		service.modify(chatRoomDTO);
		return Map.of("RESULT", "SUCCESS");
	}
	
	@DeleteMapping("/remove/{roomId}")
	public Map<String, String> remove(@PathVariable(name = "roomId") Long roomId) {
		service.remove(roomId);
		return Map.of("RESULT", "SUCCESS");
	}
	
	// ChatRoomController.java 혹은 별도 컨트롤러
	@GetMapping("/messages/{roomId}")
	public List<ChatMessageDTO> getChatMessages(@PathVariable("roomId") Long roomId) {
	    return chatMessagesService.getMessagesByRoom(roomId);
	}
	
	@PutMapping("/{roomId}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable("roomId") Long roomId, 
            @RequestParam("userId") String userId) {
            
		service.markAsRead(roomId, userId);
        return ResponseEntity.ok("Read Status Updated!");
    }
}
	   



