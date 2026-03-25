package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
	
	@Builder.Default 
	private int page= 1;
	
	@Builder.Default 
	private int size = 10; 
	
	//(관리자 검색용)
    //private Integer enabled;   // 1: 활성, 0: 삭제, null: 전체

    private String keyword;    // 댓글 내용 검색
	
}
