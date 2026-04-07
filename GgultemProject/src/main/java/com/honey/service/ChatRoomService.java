package com.honey.service;

import java.util.List;

import com.honey.dto.ChatRoomDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;

public interface ChatRoomService {

	public ChatRoomDTO get(Long roomId);

	public Long register(ChatRoomDTO chatRoomDTO);

	public PageResponseDTO<ChatRoomDTO> list(SearchDTO searchDTO);

	public void modify(ChatRoomDTO chatRoomDTO);

	public void remove(Long roomId);

	public void markAsRead(Long roomId, String userId);

	public List<ChatRoomDTO> getMyChatRooms(String userId);
	
	
}
