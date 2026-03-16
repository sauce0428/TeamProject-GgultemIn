package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {
	
	@Query("select b from Board b where enabled = 1")
	Page<Board> findAllByEnabled(Pageable pageable);
	
	@EntityGraph(attributePaths = {"boardImage"})
	@Query("SELECT b FROM Board b WHERE " +
	       "( (:searchType = 'title' AND b.title LIKE %:keyword%) OR " +
	       "  (:searchType = 'writer' AND b.writer LIKE %:keyword%) OR " +
	       "  (:searchType = 'content' AND b.content LIKE %:keyword%) OR " +
	       "  (:searchType = 'all' AND (b.title LIKE %:keyword% OR b.writer LIKE %:keyword% OR b.content LIKE %:keyword%)) ) " +
	       "OR " + // searchType이 없거나 비었을 때의 처리
	       "( (:searchType IS NULL OR :searchType = '') AND (b.title LIKE %:keyword% OR b.writer LIKE %:keyword% OR b.content LIKE %:keyword%) )")
	Page<Board> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword, Pageable pageable);

}
