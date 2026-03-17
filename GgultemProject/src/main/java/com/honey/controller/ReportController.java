//package com.honey.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.honey.dto.ReportDTO;
//import com.honey.service.ReportService;
//import com.honey.util.CustomFileUtil;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//
//@Log4j2
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/report")
//public class ReportController {
//
//    private final ReportService reportService;
//    private final CustomFileUtil fileUtil;
//
//    @PostMapping("/")
//    public Map<String, Long> register(ReportDTO reportDTO) {
//        // 1. 파일 저장 처리. 증거 스크린샷 첨부를 해도, 안 해도 되도록.
//    	if (reportDTO.getFiles() != null && !reportDTO.getFiles().isEmpty()) {
//            List<String> uploadFileNames = fileUtil.saveFiles(reportDTO.getFiles());
//            reportDTO.setUploadFileNames(uploadFileNames);
//        }
//
//        // 2. 서비스 호출
//        Long reportId = reportService.register(reportDTO);
//
//        return Map.of("REPORT_ID", reportId);
//    }
//}