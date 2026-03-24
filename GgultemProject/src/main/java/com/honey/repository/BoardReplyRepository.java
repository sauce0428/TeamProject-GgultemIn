package com.honey.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.BoardReply;

public interface BoardReplyRepository extends JpaRepository<BoardReply, Long> {

    List<BoardReply> findByBoardBoardNoAndEnabled(Integer boardNo, Integer enabled);

	List<BoardReply> findByBoardBoardNo(Integer boardNo);

	// 활성/삭제 필터
	Page<BoardReply> findByEnabled(Integer enabled, Pageable pageable);


	// 검색
	@Query("select r from BoardReply r where r.content like %:keyword%")
	Page<BoardReply> searchReply(@Param("keyword") String keyword, Pageable pageable);
	
	
}