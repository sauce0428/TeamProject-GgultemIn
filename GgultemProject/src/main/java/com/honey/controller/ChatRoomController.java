package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.ChatRoomDTO;
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
		Long no = service.register(chatRoomDTO);
		return Map.of("NO", no);
	}
}
	
