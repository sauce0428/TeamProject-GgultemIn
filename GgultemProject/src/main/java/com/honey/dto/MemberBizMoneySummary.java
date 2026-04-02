package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberBizMoneySummary {
	
	private String email;
	private Long total;
	private String type;
}
