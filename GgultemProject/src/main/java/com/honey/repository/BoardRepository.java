package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // =========================
    // 일반 사용자
    // =========================

    // 삭제 안된 게시글만
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("SELECT b FROM Board b WHERE b.enabled = 1")
    Page<Board> findAllActive(Pageable pageable);

    // 검색
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("SELECT b FROM Board b WHERE b.enabled = 1 AND (" +
            "(:searchType = 'title' AND b.title LIKE %:keyword%) OR " +
            "(:searchType = 'writer' AND b.writer LIKE %:keyword%) OR " +
            "(:searchType = 'all' AND (b.title LIKE %:keyword% OR b.writer LIKE %:keyword%)) OR " +
            "((:searchType IS NULL OR :searchType = '') AND (b.title LIKE %:keyword% OR b.writer LIKE %:keyword%))" +
            ")")
    Page<Board> searchByCondition(
            @Param("searchType") String searchType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // =========================
    // 관리자
    // =========================

    // 전체 조회 (삭제 포함)
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("SELECT b FROM Board b")
    Page<Board> findAllAdmin(Pageable pageable);

    // =========================
    // 관리자 (통합 검색) 🔥 최종 수정
    // =========================
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("""
        SELECT b FROM Board b
        WHERE (:enabled IS NULL OR b.enabled = :enabled)
        AND (:keyword IS NULL OR b.title LIKE %:keyword%)
    """)
    Page<Board> searchAllAdmin(
            @Param("enabled") Integer enabled,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}