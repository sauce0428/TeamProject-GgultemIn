package com.honey.service;

import java.util.List;

import com.honey.dto.ChatMessageDTO;

public interface ChatMessageService {

	void saveMessage(ChatMessageDTO dto);

	List<ChatMessageDTO> getMessagesByRoom(Long roomId);

}
