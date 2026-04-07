package com.honey.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Board;
import com.honey.domain.BoardReply;

public interface BoardReplyRepository extends JpaRepository<BoardReply, Long> {

	// 게시글 댓글 조회
	List<BoardReply> findByBoardBoardNoOrderByReplyNoAsc(Integer boardNo);

	List<BoardReply> findByBoardBoardNoAndEnabledOrderByReplyNoAsc(Integer boardNo, Integer enabled);
	// 검색 + 상태 + 전체 통합
	@Query("select r from BoardReply r where " + "(:enabled is null or r.enabled = :enabled) and "
			+ "(:keyword is null or r.content like %:keyword%)")
	Page<BoardReply> searchAll(@Param("enabled") Integer enabled, @Param("keyword") String keyword, Pageable pageable);
	
	// 댓글 수 카운트
	int countByBoardAndEnabled(Board board, int enabled);
} 
