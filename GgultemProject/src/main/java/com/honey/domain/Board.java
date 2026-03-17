package com.honey.domain;

import java.time.LocalDateTime;
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
@Table(name = "board") // DB테이블 이름
@SequenceGenerator(name = "BOARD_SEQ_GEN", sequenceName = "BOARD_SEQ1", initialValue = 1, allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GEN")
	@Column(name = "BOARD_NO")
	private int boardNo;

	@ManyToOne
	@JoinColumn(name = "member_email") // 실제 DB 테이블의 FK 컬럼명을 지정
	private Member member;

	private String title;
	private String writer;
	private String content;
	private int viewCount;
	
	@Builder.Default
	private Integer enabled = 1; // 1: 활성화 / 0:삭제

	@ElementCollection
	@Builder.Default
	private List<BoardImage> boardImage = new ArrayList<>();

	public void addImage(BoardImage board) {
		board.setOrd(this.boardImage.size());
		boardImage.add(board);
	}

	public void addImageString(String fileName) {
		BoardImage boardImage = BoardImage.builder().fileName(fileName).build();
		addImage(boardImage);
	}

	public void clearList() {
		this.boardImage.clear();
	}

	private LocalDateTime dtdDate;

	public void changeTitle(String title) {
		this.title = title;
	}

	public void changeWriter(String writer) {
		this.writer = writer;
	}

	public void changeViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public void changeMember(Member member) {
		this.member = member;

	}

	public void changeEnabled(int enabled) {
		this.enabled = enabled;
		switch (enabled) {
		case 1:
			this.dtdDate = null;
			break;
		case 0:
			this.dtdDate = LocalDateTime.now();
			break;
		}
	}

}
