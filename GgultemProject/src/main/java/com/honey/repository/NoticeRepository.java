package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 1. 기본 목록 조회 (삭제되지 않은 공지사항만)
    @Query("select n from Notice n where n.enabled = 1")
    Page<Notice> findAllByEnabled(Pageable pageable);

    // 2. 검색 기능 이식
    @EntityGraph(attributePaths = {"noticeImage"}) // BoardImage -> noticeImage로 변경
    @Query("SELECT n FROM Notice n WHERE " +
           "(n.enabled = 1) AND (" + // 삭제되지 않은 것들 중에서 검색
           "( (:searchType = 'title' AND n.title LIKE %:keyword%) OR " +
           "  (:searchType = 'content' AND n.content LIKE %:keyword%) OR " +
           "  (:searchType = 'all' AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)) ) " +
           "OR " + 
           "( (:searchType IS NULL OR :searchType = '') AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) )" +
           ")")
    Page<Notice> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword, Pageable pageable);
}
