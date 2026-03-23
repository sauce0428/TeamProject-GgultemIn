package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Member;

public interface BusinessMemberRepository extends JpaRepository<Member, String> {
	
	@Query("SELECT m FROM Member m WHERE m.businessNumber IS NOT NULL AND (" +
		       "(:searchType = 'email' AND m.email LIKE %:keyword%) OR " +
		       "(:searchType = 'companyName' AND m.companyName LIKE %:keyword%) OR " +
		       "(:searchType = 'businessNumber' AND m.businessNumber LIKE %:keyword%) OR " +
		       "((:searchType = 'all' OR :searchType IS NULL OR :searchType = '') AND " +
		       "(m.email LIKE %:keyword% OR m.companyName LIKE %:keyword% OR m.businessNumber LIKE %:keyword%))" +
		       ")")
		Page<Member> searchByCondition(@Param("searchType") String searchType, 
		                               @Param("keyword") String keyword, 
		                               Pageable pageable);

}
