package com.honey.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.BizMoneyHistory;

public interface BizMoneyHistoryRepository extends JpaRepository<BizMoneyHistory, Long> {
	
	///////////////////////////////////////////////////
	/// 사용자 페이지 비즈머니 내역 조회
	//////////////////////////////////////////////////
	
	@Query("SELECT bh FROM BizMoneyHistory bh " +
		       "WHERE bh.member.email = :email " +
		       "AND (:state = 'all' OR bh.type = :state) " +
		       "AND ( " + 
		       "  (:searchType = 'detail' AND bh.detail LIKE %:keyword%) OR " +
		       "  (:searchType = 'amount' AND CAST(bh.amount AS string) LIKE %:keyword%) OR " + // ✨ 여기도 % 추가
		       "  (:searchType = 'all' AND (bh.detail LIKE %:keyword% OR CAST(bh.amount AS string) LIKE %:keyword%)) OR " +
		       "  ((:searchType IS NULL OR :searchType = '') AND (bh.detail LIKE %:keyword% OR CAST(bh.amount AS string) LIKE %:keyword%)) " +
		       ") ORDER BY bh.regDate DESC")
	Page<BizMoneyHistory> searchByConditionStateFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword,
			@Param("state") String state,
			Pageable pageable,
			@Param("email") String email);
	
	@Query("SELECT bh FROM BizMoneyHistory bh " +
			"WHERE bh.member.email = :email " +
			"AND ( " + 
			"  (:searchType = 'detail' AND bh.detail LIKE %:keyword%) OR " +
			"  (:searchType = 'amount' AND CAST(bh.amount AS string) LIKE %:keyword%) OR " + // ✨ 여기도 % 추가
			"  (:searchType = 'all' AND (bh.detail LIKE %:keyword% OR CAST(bh.amount AS string) LIKE %:keyword%)) OR " +
			"  ((:searchType IS NULL OR :searchType = '') AND (bh.detail LIKE %:keyword% OR CAST(bh.amount AS string) LIKE %:keyword%)) " +
			") ORDER BY bh.regDate DESC")
	Page<BizMoneyHistory> searchByConditionAllFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword,
			Pageable pageable,
			@Param("email") String email);
	
	@Query("SELECT bh FROM BizMoneyHistory bh " + "WHERE bh.member.email = :email " + "AND bh.type = :state "
			+ "ORDER BY bh.regDate DESC")
	Page<BizMoneyHistory> findAllBizMoneyAllFilter(Pageable pageable, @Param("state") String state, @Param("email") String email);
	
	@Query("SELECT bh FROM BizMoneyHistory bh " + "WHERE bh.member.email = :email ORDER BY bh.regDate DESC")
	Page<BizMoneyHistory> findAllBizMoney(Pageable pageable, @Param("email") String email);
	
	///////////////////////////////////////////////////
	/// 관리자 페이지 비즈머니 내역 조회
	//////////////////////////////////////////////////
	
	@Query("SELECT bh FROM BizMoneyHistory bh " +
		       "WHERE (:state = 'all' OR bh.type = :state) " +
		       "AND ( " + 
		       "  (:searchType = 'detail' AND bh.detail LIKE %:keyword%) OR " +
		       "  (:searchType = 'email' AND bh.member.email LIKE %:keyword%) OR " + // ✨ String 대문자
		       "  (:searchType = 'all' AND (bh.detail LIKE %:keyword% OR bh.member.email LIKE %:keyword%)) OR " +
		       "  (COALESCE(:searchType, '') = '' AND (bh.detail LIKE %:keyword% OR bh.member.email LIKE %:keyword%)) " + // ✨ COALESCE 사용 시 더 깔끔
		       ") ORDER BY bh.regDate DESC")
		Page<BizMoneyHistory> searchByConditionStateFilterAdmin(
		        @Param("searchType") String searchType,
		        @Param("keyword") String keyword,
		        @Param("state") String state,
		        Pageable pageable);
	
	@Query("SELECT bh FROM BizMoneyHistory bh " +
			"WHERE ( " + 
			"  (:searchType = 'detail' AND bh.detail LIKE %:keyword%) OR " +
			"  (:searchType = 'email' AND bh.member.email LIKE %:keyword%) OR " + // ✨ 여기도 % 추가
			"  (:searchType = 'all' AND (bh.detail LIKE %:keyword% OR bh.member.email LIKE %:keyword%)) OR " +
			"  ((:searchType IS NULL OR :searchType = '') AND (bh.detail LIKE %:keyword% OR bh.member.email LIKE %:keyword%)) " +
			") ORDER BY bh.regDate DESC")
	Page<BizMoneyHistory> searchByConditionAllFilterAdmin(@Param("searchType") String searchType,
			@Param("keyword") String keyword,
			Pageable pageable);
	
	@Query("SELECT bh FROM BizMoneyHistory bh " + "WHERE bh.type = :state "
			+ "ORDER BY bh.regDate DESC")
	Page<BizMoneyHistory> findAllBizMoneyAllFilterAdmin(Pageable pageable, @Param("state") String state);
	
	@Query("SELECT bh FROM BizMoneyHistory bh " + "ORDER BY bh.regDate DESC")
	Page<BizMoneyHistory> findAllBizMoneyAdmin(Pageable pageable);
	
	
	///////////////////////////////////////////////////
	/// 관리자 페이지 비즈머니 회원별 통계 조회
	//////////////////////////////////////////////////
	@Query("SELECT bh.member.email as email, bh.type as type, SUM(bh.amount) as total FROM BizMoneyHistory bh " +
	       "WHERE (:state = 'all' OR bh.type = :state OR :state IS NULL) " + // state가 null일 때 대응
	       "AND ( " + 
	       "  (:searchType = 'email' AND bh.member.email LIKE '%' || :keyword || '%') OR " + 
	       "  (COALESCE(:searchType, '') = '' AND bh.member.email LIKE '%' || :keyword || '%') " +
	       ") GROUP BY bh.member.email, bh.type")
	Page<Object[]> searchByConditionStateFilterSummaryAdmin(
	        @Param("searchType") String searchType,
	        @Param("keyword") String keyword,
	        @Param("state") String state,
	        Pageable pageable);
	
	@Query("SELECT bh.member.email as email, bh.type as type, SUM(bh.amount) as total FROM BizMoneyHistory bh " +
			"WHERE ( " + 
			"  (:searchType = 'email' AND bh.member.email LIKE '%' || :keyword || '%') OR " + 
			"  (COALESCE(:searchType, '') = '' AND (bh.member.email LIKE %:keyword%)) " +
			") GROUP BY bh.member.email, bh.type")
	Page<Object[]> searchByConditionAllFilterSummaryAdmin(
			@Param("searchType") String searchType,
			@Param("keyword") String keyword,
			Pageable pageable);
	
	
	
	@Query("SELECT bh.member.email as email, bh.type as type, SUM(bh.amount) as total " +
			"FROM BizMoneyHistory bh " +
			"WHERE bh.type = :state" +
			" GROUP BY bh.member.email, bh.type")
	Page<Object[]> findAllBizMoneyAllFilterSummaryAdmin(Pageable pageable, @Param("state") String state);
	
	@Query("SELECT bh.member.email as email, bh.type as type, SUM(bh.amount) as total " +
	       "FROM BizMoneyHistory bh " +
	       "GROUP BY bh.member.email, bh.type")
	Page<Object[]> getMemberBizMoneySummaryAdmin(Pageable pageable);
	
	
	
	
	
	
	
	
	@Query("SELECT COALESCE(ABS(SUM(bh.amount)), 0) " + 
		       "FROM BizMoneyHistory bh " + 
		       "WHERE bh.member.email = :email " + 
		       "AND bh.type = 'SPEND' " + 
		       "AND bh.regDate >= :startOfToday") // ✨ 외부에서 정해준 시간을 기준으로!
		Long getTodaySpend(@Param("email") String email, @Param("startOfToday") java.time.LocalDateTime startOfToday);
	
	@Query("SELECT COALESCE(ABS(SUM(bh.amount)), 0) " + 
			"FROM BizMoneyHistory bh " + 
			"WHERE bh.member.email = :email " + 
			"AND bh.type = 'SPEND' ") // ✨ 외부에서 정해준 시간을 기준으로!
	Long getTotalSpend(@Param("email") String email);
	


}
