package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.SearchLog;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
	@Query("SELECT sl FROM SearchLog sl WHERE " +
		       "( (:searchType = 'keyword' AND sl.keyword LIKE %:keyword%) OR " +
		       "  (:searchType = 'searchType' AND sl.searchType LIKE %:keyword%) OR " +
		       "  (:searchType = 'all' AND (sl.keyword LIKE %:keyword% OR sl.searchType LIKE %:keyword%)) ) " +
		       "OR " + // searchType이 없거나 비었을 때의 처리
		       "( (:searchType IS NULL OR :searchType = '') AND (sl.keyword LIKE %:keyword% OR sl.searchType LIKE %:keyword%) )")
	Page<SearchLog> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword, Pageable pageable);
	
}
