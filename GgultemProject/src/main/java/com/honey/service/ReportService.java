package com.honey.service;

import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ReportDTO;

public interface ReportService {
    // 신고 등록
    Long register(ReportDTO reportDTO);

    // 신고 목록 (나중에 신고자가 자신의 신고내역을 열람할 때를 대비한 조회용 메서드)
    PageResponseDTO<ReportDTO> list(PageRequestDTO pageRequestDTO);
    
    
}