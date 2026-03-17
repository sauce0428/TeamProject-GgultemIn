package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardReplyDTO {
	// 댓글번호
	private Long replyNo;
	// 게시물 번호
	private Integer boardNo;
	// 회원이메일
	private String email;
	// 댓글내용
	private String content;
	// 대댓글 번호
	private Long parentReplyNo;
	
	//대댓글 구조
	@Builder.Default
    private List<BoardReplyDTO> childList = new ArrayList<>();
	//삭제된 댓글 처리용 
    private Integer enabled;
	
	// 입력날짜/수정날짜
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
	
	

	
}