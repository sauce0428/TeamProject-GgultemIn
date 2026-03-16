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
@Table(name = "searchLog")
@SequenceGenerator(name = "SEARCHLOG_SEQ_GEN",
sequenceName = "SEARCHLOG_SEQ",
 initialValue = 1,
 allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchLog extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEARCHLOG_SEQ_GEN")
	private Long no;
	
	private String keyword;
	private String searchType;

}
