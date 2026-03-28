package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    
    private Long roomId;    // ✨ 어느 방 메시지인지 식별하기 위해 추가
    private String senderId; // ✨ Entity와 이름을 맞췄습니다 (기존 nickname)
    private String content;
    private Integer isRead;     // ✨ 읽음 처리 여부
    private String regDate;  // ✨ 화면에 뿌려줄 생성 시간 (String 혹은 LocalDateTime)

}