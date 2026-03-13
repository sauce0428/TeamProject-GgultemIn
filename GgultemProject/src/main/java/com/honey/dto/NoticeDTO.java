package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honey.domain.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {
	
    private Long noticeId;
    private String title;
    private String content;
    private Integer enabled;
    private Long memberNo;
    private Integer viewCount;
	
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dtdDate;
	
	
	@Builder.Default
	private List<MultipartFile> files = new ArrayList<>();
	
	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();
	
	
}
