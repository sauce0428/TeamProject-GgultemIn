package com.honey.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Builder
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "CART_SEQ_GEN", sequenceName = "CART_SEQ", allocationSize = 1, initialValue = 1)
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "CART_SEQ_GEN")
	@Column(name = "CART_ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "ITEMBOARD_ID") // 실제 DB 테이블의 FK 컬럼명을 지정
	private ItemBoard itemBoard;
	
	@ManyToOne
	@JoinColumn(name = "MEMBER_NO") // 실제 DB 테이블의 FK 컬럼명을 지정
	private Member member;
}
