package com.honey.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BizMoneyHistoryDTO {

    private Long hno; // 내역 번호

    private String email; // 누구의 돈인가?

    private Long amount; // 변동 금액 (충전은 +, 지출은 -)

    private Long balance; // 변동 후 최종 잔액 (스냅샷)

    private String type; // 거래 유형 (CHARGE: 충전, SPEND: 광고지출, REFUND: 환불)

    private String detail; // 상세 내용 (예: "상품 광고 클릭 지출", "카카오페이 충전")
    
    private Long total;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
	
}
