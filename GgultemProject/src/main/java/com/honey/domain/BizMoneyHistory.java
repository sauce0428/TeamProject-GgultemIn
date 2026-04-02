package com.honey.domain;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "member")
public class BizMoneyHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hno; // 내역 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_email")
    private Member member; // 누구의 돈인가?

    private Long amount; // 변동 금액 (충전은 +, 지출은 -)

    private Long balance; // 변동 후 최종 잔액 (스냅샷)

    private String type; // 거래 유형 (CHARGE: 충전, SPEND: 광고지출, REFUND: 환불)

    private String detail; // 상세 내용 (예: "상품 광고 클릭 지출", "카카오페이 충전")
    
    private Long total;
}
