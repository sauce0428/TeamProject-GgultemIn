package com.honey.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honey.domain.Member;
import com.honey.domain.Report;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ReportDTO;
import com.honey.repository.MemberRepository;
import com.honey.repository.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long register(ReportDTO reportDTO) {
        
     // 검증 로직 추가: 기타 신고인데 사유가 없는 경우
        if (Report.TYPE_ETC.equals(reportDTO.getReportType()) && 
           (reportDTO.getReason() == null || reportDTO.getReason().trim().isEmpty())) {
            throw new IllegalArgumentException("기타 신고 시 상세 사유를 입력해야 합니다.");
        }
        
        boolean exists = reportRepository.existsByReporter_EmailAndTargetTypeAndTargetNo(
        	    reportDTO.getMemberEmail(),
        	    reportDTO.getTargetType(),
        	    reportDTO.getTargetNo()
        	);
        	if (exists) {
        	    throw new IllegalStateException("이미 신고한 게시물입니다.");
        	}

        // 1. 신고자(Member) 엔티티 조회
        // DTO의 memberNo를 사용하여 DB에서 실제 회원 객체를 가져옵니다.
//        Member reporter = memberRepository.findById(reportDTO.getMemberNo())
//                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));
        Member member = Member.builder().email(reportDTO.getMemberEmail()).build();
        

        Report report = Report.builder()
                .reporter(member)  				   // 신고자 (Member 객체)
                .targetMemberId(reportDTO.getTargetMemberId()) // 신고대상자 (String ID)
                .targetType(reportDTO.getTargetType()) // "코멘트", "게시판", "채팅"
                .reportType(reportDTO.getReportType()) // "욕설", "사기", "기타" 등
                .reason(reportDTO.getReason())         // "기타"일 때만 내용이 있고 나머진 null일 수 있음
                .status(0)							   // 초기상태 : 접수됨(0)
                .targetNo(reportDTO.getTargetNo())     // 신고된 게시글이나 코멘트의 no
                .build();

        // 3. 이미지 파일 이름 처리
        // ReportDTO의 uploadFileNames 리스트를 순회하며 엔티티에 추가합니다.
        List<String> uploadFileNames = reportDTO.getUploadFileNames();
        
        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(fileName -> {
                report.addImageString(fileName);
            });
        }

        // 4. DB 저장
        Report savedReport = reportRepository.save(report);

        log.info("신고 등록 완료. 생성된 신고 번호: {}", savedReport.getReportId());

        return savedReport.getReportId();
    }

    @Override
    public PageResponseDTO<ReportDTO> list(PageRequestDTO pageRequestDTO) {
        // 차후 신고목록 조회 기능을 추가.
        log.info("신고 목록 조회 기능 준비중");
        return null;
    }
}