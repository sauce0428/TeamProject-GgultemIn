package com.honey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honey.domain.Member;
import com.honey.domain.ProcessedReport;
import com.honey.domain.Report;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ProcessedReportDTO;
import com.honey.dto.ReportDTO;
import com.honey.repository.MemberRepository;
import com.honey.repository.ProcessedReportRepository;
import com.honey.repository.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ProcessedReportServiceImpl implements ProcessedReportService {
    private final ProcessedReportRepository processedRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long process(ProcessedReportDTO dto) {
        Report report = reportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 내역입니다."));

        Member targetMember = memberRepository.findById(report.getTargetMemberId())
                .orElse(null);

        if (targetMember == null) {
            log.warn("피신고자({})를 찾을 수 없어 정지 처리를 건너뜁니다.", report.getTargetMemberId());
        } else if (dto.getMemberStatus() != null) {
            targetMember.changeStatus(dto.getMemberStatus());
            memberRepository.save(targetMember);
        }

        Member admin = memberRepository.findById(dto.getAdminEmail())
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        ProcessedReport processed = ProcessedReport.builder()
                .report(report)
                .admin(admin)
                .actionNote(dto.getActionNote())
                .reportStatus(dto.getReportStatus())
                .build();

        processedRepository.save(processed);
        report.changeStatus(1);
        return processed.getProcessedReportId();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ReportDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("reportId").descending()
        );
        Page<Report> result = reportRepository.findAll(pageable);
        List<ReportDTO> dtoList = result.getContent().stream()
                .map(report -> ReportDTO.builder()
                        .reportId(report.getReportId())
                        .memberEmail(report.getReporter().getEmail())
                        .targetMemberId(report.getTargetMemberId())
                        .targetType(report.getTargetType())
                        .reportType(report.getReportType())
                        .reason(report.getReason())
                        .status(report.getStatus())
                        .targetNo(report.getTargetNo())
                        .regDate(report.getRegDate())
                        .build())
                .collect(Collectors.toList());
        return PageResponseDTO.<ReportDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDTO getOne(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고입니다."));
        return ReportDTO.builder()
                .reportId(report.getReportId())
                .memberEmail(report.getReporter().getEmail())
                .targetMemberId(report.getTargetMemberId())
                .targetType(report.getTargetType())
                .reportType(report.getReportType())
                .reason(report.getReason())
                .status(report.getStatus())
                .targetNo(report.getTargetNo())
                .regDate(report.getRegDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessedReportDTO getOneProcessed(Long reportId) {
        ProcessedReport processed = processedRepository.findByReport_ReportId(reportId)
                .orElseThrow(() -> new IllegalArgumentException("처리 내역이 존재하지 않습니다."));
        return ProcessedReportDTO.builder()
                .reportId(processed.getReport().getReportId())
                .adminEmail(processed.getAdmin().getEmail())
                .actionNote(processed.getActionNote())
                .reportStatus(processed.getReportStatus())
                .build();
    }
}