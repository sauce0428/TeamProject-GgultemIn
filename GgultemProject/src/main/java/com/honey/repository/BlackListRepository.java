package com.honey.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.BlackList;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    
    // 1. 중복 차단 체크를 위한 메서드
    // 입력된 이메일이 이미 'Y'(차단 중) 상태인지 확인합니다.
    Optional<BlackList> findByEmailAndStatus(String email, String status);

    // 2. 이메일 키워드 검색 (부분 일치)
    // SQL: SELECT * FROM blacklist WHERE email LIKE %keyword%
    Page<BlackList> findByEmailContaining(String email, Pageable pageable);

    // 3. 차단 사유 키워드 검색 (부분 일치)
    // SQL: SELECT * FROM blacklist WHERE reason LIKE %keyword%
    Page<BlackList> findByReasonContaining(String reason, Pageable pageable);

    // 기존에 사용하던 활성화 데이터 조회용 쿼리 (필요 시 유지)
    @Query("select c from BlackList c where c.enabled = 1")
    Page<BlackList> findAllByEnabled(Pageable pageable);
}