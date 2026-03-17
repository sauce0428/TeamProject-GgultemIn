package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

	private Integer boardNo;

	private String email; // 작성자 이메일

	private String title;

	private String writer;

	private String content;

	@Builder.Default
	private Integer viewCount = 0;

	@Builder.Default
	private Integer enabled = 1;

	// 새로 업로드할 파일들
	@Builder.Default
	private List<MultipartFile> files = new ArrayList<>();

	// 서버에 저장된 파일명 리스트
	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dtdDate;

	

}