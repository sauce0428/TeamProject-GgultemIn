package com.honey.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "business_board")
@SequenceGenerator(name = "BUSINESS_BOARD_SEQ_GEN",
sequenceName = "BUSINESS_BOARD_SEQ",
initialValue = 1,
allocationSize = 1)
@Getter
@ToString(exclude = {"writer", "bItemList"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessBoard extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BUSINESS_BOARD_SEQ_GEN")
	private Long no;
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private int price;
	
	@Column(nullable = false)
	private String category;
	
	@Column(nullable = false)
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 권장
    @JoinColumn(name = "MEMBER_EMAIL") // DB 컬럼명
    private Member writer;
	
	private LocalDateTime dtdDate;
	private LocalDateTime endDate;
	private int enabled;
	private char sign;
	
	@ElementCollection 
	@Builder.Default 
	private List<BusinessItem> bItemList = new ArrayList<>(); 
	
	public void changeTitle(String title) {
		this.title = title;
	}
	public void changeContent(String content) {
		this.content = content;
	}
	public void changePrice(int price) {
		this.price = price;
	}
	public void changeCategory(String category) {
		this.category = category;
	}
	public void setWriter(Member member) {
		this.writer = member;
	}
	public void changeEnabled(int enabled) {
		this.enabled = enabled;
		LocalDateTime now = LocalDateTime.now();
		
		if(enabled == 0) {
			this.dtdDate = now;
		}
	}
	public void changeSign(char sign) {
        this.sign = sign;
    }
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	public void addImage(BusinessItem image) {
		image.setOrd(this.bItemList.size());
		bItemList.add(image);
	}
	public void addImageString(String fileName) {
		BusinessItem bItem = BusinessItem.builder().fileName(fileName).build();
		addImage(bItem);
	}
	public void clearList() {
		this.bItemList.clear();
	}
}
