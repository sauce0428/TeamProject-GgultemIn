package com.honey.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Member;

import jakarta.transaction.Transactional;

public interface MemberRepository extends JpaRepository<Member, String> {

	List<Member> findByStopEndDateBeforeAndEnabledIn(LocalDateTime now, List<Integer> statuses);
	
	@EntityGraph(attributePaths = {"thumbnailList"})
	@Query("SELECT m FROM Member m WHERE " +
	       "( (:searchType = 'id' AND m.id LIKE %:keyword%) OR " +
	       "  (:searchType = 'nickname' AND m.nickname LIKE %:keyword%) OR " +
	       "  (:searchType = 'all' AND (m.id LIKE %:keyword% OR m.nickname LIKE %:keyword%)) ) " +
	       "OR " + // searchType이 없거나 비었을 때의 처리
	       "( (:searchType IS NULL OR :searchType = '') AND (m.id LIKE %:keyword% OR m.nickname LIKE %:keyword%) )")
	Page<Member> searchByCondition(@Param("searchType") String searchType, 
	                               @Param("keyword") String keyword, 
	                               Pageable pageable);
	
	@EntityGraph(attributePaths = { "memberRoleSet" }) 
	@Query("select m from Member m where m.email = :email") 
	Member getWithRoles(@Param("email") String email);
	
	@Modifying(clearAutomatically = true)
	@Transactional 
	@Query("update Member m set m.businessNumber = :bn, m.companyName = :cn where m.email = :email")
	int businessRegister(@Param("bn") String bn, @Param("cn") String cn, @Param("email") String email);
	
}
