package com.honey.service;

import com.honey.dto.ChatRoomDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;

public interface ChatRoomService {

	public ChatRoomDTO get(Long roomId);

	public Long register(ChatRoomDTO chatRoomDTO);

	public PageResponseDTO<ChatRoomDTO> list(PageRequestDTO pageRequestDTO);

	public void modify(ChatRoomDTO chatRoomDTO);

	public void remove(Long roomId);
	
	
}
