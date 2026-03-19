package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeDetailDTO {
	private String groupCode;
	private String codeValue;
	private String codeName;
	private Integer sortSeq;
	private String useYn;
	private Integer enabled;
}