package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.ChatRoomDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/chatroom")
public class ChatRoomController {

	private final ChatRoomService service;
	
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
	public PageResponseDTO<ChatRoomDTO> list(PageRequestDTO pageRequestDTO) {
		return service.list(pageRequestDTO);
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
}
	   



