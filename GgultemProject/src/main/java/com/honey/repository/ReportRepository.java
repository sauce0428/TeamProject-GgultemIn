package com.honey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
//	// Member -> Reporter로 변경
//    List<Report> findByReporter_MemberNo(Long no);
//    
//    // 동일한 유저가 같은 게시글을 중복 신고하는걸 막고 싶을 때
//    boolean existsByReporter_MemberNoAndTargetTypeAndTargetNo(Long no, String targetType, Long targetNo);
	
	
	
	    
	List<Report> findByReporter_Email(String email);
	    
	 // 동일한 유저가 같은 게시글을 중복 신고하는걸 막고 싶을 때
	// 1. 변수명인 'reporter'를 따라간다.
    // 2. Member의 PK가 email이므로 _Email을 붙인다.
    boolean existsByReporter_EmailAndTargetTypeAndTargetNo(String email, String targetType, Long targetNo);
}