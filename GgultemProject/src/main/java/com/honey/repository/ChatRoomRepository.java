package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	
	@Query("select c from ChatRoom c where enabled = 1")
	Page<ChatRoom> findAllByEnabled(Pageable pageable);

}
