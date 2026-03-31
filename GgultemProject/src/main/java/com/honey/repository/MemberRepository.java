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
			"( (:searchType = 'email' AND m.email LIKE %:keyword%) OR " +
			"  (:searchType = 'nickname' AND m.nickname LIKE %:keyword%) OR " +
			"  (:searchType = 'all' AND (m.email LIKE %:keyword% OR m.nickname LIKE %:keyword%)) ) " +
			"OR " +
			"( (:searchType IS NULL OR :searchType = '') AND (m.email LIKE %:keyword% OR m.nickname LIKE %:keyword%) )")
	Page<Member> searchByCondition(@Param("searchType") String searchType, 
			@Param("keyword") String keyword,
			Pageable pageable);
	
	@EntityGraph(attributePaths = {"thumbnailList"})
	@Query("SELECT m FROM Member m WHERE " +
	       "( (:searchType = 'email' AND m.email LIKE %:keyword% AND m.enabled = :enabled) OR " +
	       "  (:searchType = 'nickname' AND m.nickname LIKE %:keyword% AND m.enabled = :enabled) OR " +
	       "  (:searchType = 'all' AND ((m.email LIKE %:keyword% OR m.nickname LIKE %:keyword%) AND m.enabled = :enabled)) ) " +
	       "OR " +
	       "( (:searchType IS NULL OR :searchType = '') AND (m.email LIKE %:keyword% OR m.nickname LIKE %:keyword% OR m.enabled = :enabled) )")
	Page<Member> searchByConditionFilter(@Param("searchType") String searchType, 
	                               @Param("keyword") String keyword,
	                               @Param("enabled") Integer enabled,
	                               Pageable pageable);
	
	@Query("SELECT m FROM Member m WHERE m.enabled = :enabled")
	Page<Member> findAllFilter(Pageable pageable, @Param("enabled") Integer enabled);
	
	
	@EntityGraph(attributePaths = { "memberRoleSet" }) 
	@Query("select m from Member m where m.email = :email") 
	Member getWithRoles(@Param("email") String email);
	
	@Modifying(clearAutomatically = true)
	@Transactional 
	@Query("update Member m set m.businessNumber = :bn, m.companyName = :cn where m.email = :email")
	int businessRegister(@Param("bn") String bn, @Param("cn") String cn, @Param("email") String email);
	
	@Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.email = :email")
	boolean existsByEmail(@Param("email") String email);
	
	@Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.nickname = :nickname")
	boolean existsByNickname(@Param("nickname") String nickname);
}
