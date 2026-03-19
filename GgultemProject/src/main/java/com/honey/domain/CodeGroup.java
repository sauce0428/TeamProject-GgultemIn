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
@Table(name = "codegroup")
@SequenceGenerator(name = "CODEGROUP_SEQ_GEN", sequenceName = "CODEGROUP_SEQ", initialValue = 1, allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeGroup extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CODEGROUP_SEQ_GEN")
	private Long groupCode;
	private String groupName;
	private String useYn;
	private Integer enabled;

	public void changeEnabled(int enabled) {
		this.enabled = enabled;
	}

	public void changeGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setGroupCode(Long groupCode) {
		this.groupCode = groupCode;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

}
