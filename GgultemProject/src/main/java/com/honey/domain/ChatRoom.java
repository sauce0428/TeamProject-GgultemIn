package com.honey.domain;

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
@Table(name = "chatroom")
@SequenceGenerator(name = "CHATROOM_SEQ_GEN",
		sequenceName = "CHATROOM_SEQ",
		initialValue = 1,
		allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHATROOM_SEQ_GEN")
	private Long roomId;
	
	@ManyToOne
	@JoinColumn(name = "ITEMBOARD_ID") // 실제 DB 테이블의 FK 컬럼명을 지정
	private ItemBoard itemboard;
	private String roomName;
	private String buyerId;
	private String sellerId;
	private Integer enabled; // 1:활성화, 0:삭제
	
	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "chat_messages")
	@Builder.Default
	private List<ChatMessages> chatMessages = new ArrayList();
	
	public void changeEnabled(int enabled) {
		this.enabled = enabled;
	}
	
	public void changeRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	
}
