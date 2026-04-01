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
@NoArgsConstructor
@AllArgsConstructor
public class ItemBoardDTO {

	private Long id;
	private Member member;
	private String email;
	private String title;
	private String writer;
	private int price;
	private String content;
	private String category;
	private String status;
	private String location;
	private String itemUrl;
	private String pictureUrl;
	private Integer enabled;
	private Integer viewCount;
	// 별표 체크 유지
	private boolean isFavorite;

	// 주소(위도,경도)
	private Double lat;
	private Double lng;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;

	@Builder.Default
	private List<MultipartFile> files = new ArrayList<>();

	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();
}
