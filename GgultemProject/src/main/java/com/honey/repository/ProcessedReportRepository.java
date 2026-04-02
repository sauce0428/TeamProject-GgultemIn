package com.honey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.ProcessedReport;

public interface ProcessedReportRepository extends JpaRepository<ProcessedReport, Long> {
    Optional<ProcessedReport> findByReport_ReportId(Long reportId); // ✅ 추가
}