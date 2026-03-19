package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeGroupDTO {
	private Long groupCode;
	private String groupName;
	private String useYn;
	private Integer enabled;
}