package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockListDTO {
	private Long no;
	private String memberEmail;
	private String blockId;
	private String reason;
	private Integer enabled;
}