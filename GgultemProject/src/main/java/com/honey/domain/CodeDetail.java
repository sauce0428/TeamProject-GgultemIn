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
@Table(name = "codeDetail")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeDetail extends BaseTimeEntity {
	@Id
	private String groupCode;
	private String codeValue;
	private String codeName;
	private Integer sortSeq;
	private String useYn;
	private Integer enabled;

	public void changeEnabled(int enabled) {
		this.enabled = enabled;
	}

	public void changeCodeName(String codeName) {
		this.codeName = codeName;
	}

}
