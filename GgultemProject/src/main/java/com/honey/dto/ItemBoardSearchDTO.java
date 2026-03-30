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
public class ItemBoardSearchDTO extends PageRequestDTO {
	
	private String searchType;
	private String keyword;
	private String email;
	@Builder.Default
    private String status = "all";
    
    @Builder.Default
    private String category = "all";
    
    @Builder.Default
    private String location = "all";
    
    private Integer enabled;
}
