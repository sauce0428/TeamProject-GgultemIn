package com.honey.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
	private Long roomId;
	private Long itemId;
	private String roomName;
	private String buyerId;
	private String sellerId;
	private Integer enabled;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dtdDate;
	
	// ✨ 추가해야 할 필드
    private String lastMessage;     // 마지막 대화 내용
    private LocalDateTime lastSendTime; // 마지막 대화 시간
    private Long unReadCount;      // 내가 안 읽은 메시지 개수
}