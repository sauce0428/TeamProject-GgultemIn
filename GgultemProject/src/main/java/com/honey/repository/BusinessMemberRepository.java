package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.BusinessMember;

public interface BusinessMemberRepository extends JpaRepository<BusinessMember, Long> {
	
	@Query("SELECT bm FROM BusinessMember bm WHERE " +
	       "( (:searchType = 'id' AND bm.id LIKE %:keyword%) OR " +
	       "  (:searchType = 'businessName' AND bm.businessName LIKE %:keyword%) OR " +
	       "  (:searchType = 'all' AND (bm.id LIKE %:keyword% OR bm.businessName LIKE %:keyword%)) ) " +
	       "OR " + // searchType이 없거나 비었을 때의 처리
	       "( (:searchType IS NULL OR :searchType = '') AND (bm.id LIKE %:keyword% OR bm.businessName LIKE %:keyword%) )")
	Page<BusinessMember> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword, Pageable pageable);

}
