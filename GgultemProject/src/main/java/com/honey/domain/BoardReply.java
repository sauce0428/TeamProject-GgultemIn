package com.honey.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reply")
@SequenceGenerator(name = "REPLY_SEQ_GEN", sequenceName = "REPLY_SEQ1", allocationSize = 1)
@Getter
@ToString(exclude = { "board", "member", "parent", "childList" })
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardReply extends BaseTimeEntity {

	@Id
	@Column(name = "REPLY_NO")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPLY_SEQ_GEN")
	private Long replyNo;

	// 게시글
	@ManyToOne
	@JoinColumn(name = "BOARD_NO_BOARD", nullable = false)
	private Board board;

	// 작성자
	@ManyToOne
	@JoinColumn(name = "NO_MEMBER", nullable = false)
	private Member member;

	private String content;

	// 부모 댓글 (대댓글)
	@ManyToOne
	@JoinColumn(name = "PARENT_REPLY_NO", nullable = true)
	private BoardReply parent;

	// 대댓글 리스트
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	@Builder.Default
	private List<BoardReply> childList = new ArrayList<>();

	// 삭제 여부 / 데이터는 남기는 역할
	private Integer enabled; // 1: 활성 / 0: 삭제

	private LocalDateTime dtdDate;

	public void changeContent(String content) {
		this.content = content;
	}

	public void changeEnabled(int enabled) {
		switch (enabled) {
		case 1:
			this.dtdDate = null;
			break;
		case 0:
			this.enabled = enabled;
			this.dtdDate = LocalDateTime.now();
			break;
		}
	}
}