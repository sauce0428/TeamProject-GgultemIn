package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.BusinessBoard;

public interface BusinessBoardRepository extends JpaRepository<BusinessBoard, Long> {
	
	@EntityGraph(attributePaths = {"writer", "bItemList"})
	@Query("SELECT bb FROM BusinessBoard bb WHERE " +
		       "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR " +
		       "  (:searchType = 'category' AND bb.category LIKE %:keyword%) OR " +
		       "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR " +
		       "  (:searchType = 'sign' AND bb.sign LIKE %:keyword%) OR " +
		       "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.category LIKE %:keyword% OR bb.content LIKE %:keyword% OR bb.sign LIKE %:keyword%)) ) " +
		       "OR " + // searchType이 없거나 비었을 때의 처리
		       "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.category LIKE %:keyword% OR bb.content LIKE %:keyword% OR bb.sign LIKE %:keyword%) )")
	Page<BusinessBoard> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword, Pageable pageable);
	
}
