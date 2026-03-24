package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO extends PageRequestDTO {
	
	private String searchType;
	private String keyword;
	
	//관리자용
	private String enabled;
}
