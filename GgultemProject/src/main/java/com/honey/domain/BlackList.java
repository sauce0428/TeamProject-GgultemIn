package com.honey.domain;

import java.time.LocalDateTime;

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
@Table(name = "blacklist")
@SequenceGenerator(name = "BLACKLIST_SEQ_GEN",
		sequenceName = "BLACKLIST_SEQ",
		initialValue = 1,
		allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlackList extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BLACKLIST_SEQ_GEN")
	private Long blId;
	private String email;
	private String reason;
	private String adminId;
	private String status; // 활성 상태 (Y/N)
	private LocalDateTime startDate; 
	private LocalDateTime endDate; // (NULL이면 영구)
	private Integer enabled; // 1:활성화, 0:삭제
	
	public void changeEnabled(int enabled) {
		this.enabled = enabled;
	}
	
	public void changeEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public void setBlId(Long blId) {
		this.blId = blId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}
}
