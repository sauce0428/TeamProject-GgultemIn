package com.honey.domain;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "blocklist")
@SequenceGenerator(name = "BLOCKLIST_SEQ_GEN",
		sequenceName = "BLOCKLIST_SEQ",
		initialValue = 1,
		allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlockList extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BLOCKLIST_SEQ_GEN")
	private Long no;
	private String memberEmail;
	private String blockId;
	private String reason;
	private Integer enabled;
	
	public void changeEnabled(int enabled) {
		this.enabled = enabled;
	}
	
	public void changeReason(String reason) {
		this.reason = reason;
	}

	public void setNo(Long no) {
		this.no = no;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
	
	
}
