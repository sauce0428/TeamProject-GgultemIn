//package com.honey.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.honey.domain.Report;
//
//public interface ReportRepository extends JpaRepository<Report, Long> {
////	// Member -> Reporter로 변경
////    List<Report> findByReporter_MemberNo(Long no);
////    
////    // 동일한 유저가 같은 게시글을 중복 신고하는걸 막고 싶을 때
////    boolean existsByReporter_MemberNoAndTargetTypeAndTargetNo(Long no, String targetType, Long targetNo);
//	
//	    
//	    List<Report> findByReporter_No(Long no);
//	    
//	 // 동일한 유저가 같은 게시글을 중복 신고하는걸 막고 싶을 때
//	    boolean existsByReporter_NoAndTargetTypeAndTargetNo(Long no, String targetType, Long targetNo);
//}