package com.honey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.ChatMessages;

public interface ChatMessageRepository extends JpaRepository<ChatMessages, Long> {

    // 특정 방(roomId)의 모든 메시지를 보낸 시간 순으로 조회
    List<ChatMessages> findByChatRoomRoomIdOrderByRegDateAsc(Long roomId);
}
