package com.honey.domain;

import java.util.ArrayList;
import java.util.List;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name = "itemBoard")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "ITEMBOARD_SEQ_GEN", sequenceName = "ITEMBOARD_SEQ", initialValue = 1, allocationSize = 1)
public class ItemBoard extends BaseTimeEntity {

	@Id
	@GeneratedValue(generator = "ITEMBOARD_SEQ_GEN", strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "MEMBER_NO") // 실제 DB 테이블의 FK 컬럼명을 지정
	private Member member;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String writer;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private String category;

	private String status;

	@Column(nullable = false)
	private String location;
	
	private String itemUrl;
	private String pictureUrl;
	private int viewCount;

	@Builder.Default
	@ElementCollection
	private List<ItemBoardImage> itemList = new ArrayList<>();

	public void changeTitle(String title) {
		this.title = title;
	}

	public void changeWriter(String writer) {
		this.writer = writer;
	}

	public void changePrice(int price) {
		this.price = price;
	}

	public void changeContent(String content) {
		this.content = content;
	}

	public void changeCategory(String category) {
		this.category = category;
	}

	public void changeStatus(String status) {
		this.status = status;
	}

	public void changeLocation(String location) {
		this.location = location;
	}

	public void addImage(ItemBoardImage image) {
		// ModelMapper 등으로 인해 itemList가 null이 된 경우를 대비해 직접 초기화
		if (this.itemList == null) {
			this.itemList = new ArrayList<>();
		}
		image.setOrd(this.itemList.size());
		itemList.add(image);
	}

	public void addImageString(String fileName) {
		ItemBoardImage image = ItemBoardImage.builder().fileName(fileName).build();
		addImage(image);
	}

	public void clearList() {
		this.itemList.clear();
	}

}
