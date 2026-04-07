package com.honey.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.ChatMessages;
import com.honey.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	
	@Query("select c from ChatRoom c where enabled = 1")
	Page<ChatRoom> findAllByEnabled(Pageable pageable);

	@Query("SELECT b FROM ChatRoom b WHERE " +
	       "( (:searchType = 'roomName' AND b.roomName LIKE %:keyword%) OR " +
	       "  (:searchType = 'buyerId' AND b.buyerId LIKE %:keyword%) OR " +
	       "  (:searchType = 'sellerId' AND b.sellerId LIKE %:keyword%) OR " +
	       "  (:searchType = 'all' AND (b.roomName LIKE %:keyword% OR b.buyerId LIKE %:keyword% OR b.sellerId LIKE %:keyword%)) ) " +
	       "OR " + // searchType이 없거나 비었을 때의 처리
	       "( (:searchType IS NULL OR :searchType = '') AND (b.roomName LIKE %:keyword% OR b.buyerId LIKE %:keyword% OR b.sellerId LIKE %:keyword%) )")
	Page<ChatRoom> searchByCondition(@Param("searchType")String searchType, @Param("keyword") String keyword, Pageable pageable);
	
	@Query("SELECT c FROM ChatRoom c WHERE c.buyerId = :buyerId AND c.sellerId = :sellerId AND c.itemId = :itemId")
	Optional<ChatRoom> findByBuyerIdAndSellerIdAndItemId(String buyerId, String sellerId, Long itemId);
	
	

 // ✅ 내가 참여 중(판매자 OR 구매자)이고, 삭제되지 않은(enabled=1) 채팅방 조회
    @Query("SELECT r FROM ChatRoom r " +
           "WHERE (r.sellerId = :userId OR r.buyerId = :userId) " +
           "AND r.enabled = 1 " +
           "ORDER BY r.regDate DESC") // 기본적으로 생성일 순 정렬 (나중에 메시지 순으로 재정렬)
    List<ChatRoom> findActiveRoomsByUserId(@Param("userId") String userId);

}
