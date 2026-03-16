package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {
    private Long reportId;
    private Long memberNo;
    private String targetMemberId;
    private String reportType;
    private String targetType; // 0, 1, 2, 3 등
    private String reason;
    private Integer status;
    private Long targetNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    // 파일 업로드를 위한 필드 (NoticeDTO와 동일)
    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();
    
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();
}