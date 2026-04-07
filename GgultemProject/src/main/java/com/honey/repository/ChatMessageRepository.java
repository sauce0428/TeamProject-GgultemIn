package com.honey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.ChatMessages;
import com.honey.domain.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessages, Long> {

    // 특정 방(roomId)의 모든 메시지를 보낸 시간 순으로 조회
    List<ChatMessages> findByChatRoomRoomIdOrderByRegDateAsc(Long roomId);
    
 // 1. 특정 방의 마지막 메시지 한 개 가져오기
    Optional<ChatMessages> findTopByChatRoomOrderByRegDateDesc(ChatRoom chatRoom);

    // 2. 특정 방에서 '나'가 아닌 상대방이 보낸 메시지 중 안 읽은(isRead=0) 개수 카운트
    @Query("SELECT COUNT(m) FROM ChatMessages m " +
           "WHERE m.chatRoom.roomId = :roomId " +
           "AND m.senderId != :userId " +
           "AND m.isRead = 0")
    Long countUnreadMessages(@Param("roomId") Long roomId, @Param("userId") String userId);
  

        // ✅ 벌크 업데이트: 상대방이 보낸 메시지만 1(읽음)로 변경
        @Modifying(clearAutomatically = true) // 쿼리 실행 후 영속성 컨텍스트를 비워줌 (데이터 정합성 유지 🍯)
        @Query("UPDATE ChatMessages m " +
               "SET m.isRead = 1 " +
               "WHERE m.chatRoom.roomId = :roomId " +
               "AND m.senderId != :userId " +
               "AND m.isRead = 0")
        int updateReadStatus(@Param("roomId") Long roomId, @Param("userId") String userId);
    
}
