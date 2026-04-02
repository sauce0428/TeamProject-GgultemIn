package com.honey.service;

import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ProcessedReportDTO;
import com.honey.dto.ReportDTO;

public interface ProcessedReportService {
    Long process(ProcessedReportDTO dto);
    PageResponseDTO<ReportDTO> list(PageRequestDTO pageRequestDTO);
    ReportDTO getOne(Long reportId);
    ProcessedReportDTO getOneProcessed(Long reportId);
}
