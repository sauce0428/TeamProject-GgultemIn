package com.honey.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "business_member", uniqueConstraints = {
	    @UniqueConstraint(name = "UK_BMEMBER_ID", columnNames = "ID"),
	    @UniqueConstraint(name = "UK_BMEMBER_BRN", columnNames = "BRN"),
	    @UniqueConstraint(name = "UK_BMEMBER_BNAME", columnNames = "BUSINESS_NAME")
	})
@SequenceGenerator(name = "BUSINESS_MEMBER_SEQ_GEN",
		sequenceName = "BUSINESS_MEMBER_SEQ",
		initialValue = 1,
		allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessMember extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BUSINESS_MEMBER_SEQ_GEN")
	private Long no;
	
	@Column(nullable = false)
	private String id;
	
	private String pw;
	
	@Column(nullable = false)
	private String brn;
	
	@Column(nullable = false)
	private String businessName;
	
	private int coin;
	private Integer enabled;
	private LocalDateTime dtdDate;   // 삭제일
	private LocalDateTime stopDate;  // 정지 시작일
    private LocalDateTime stopEndDate;  // 정지 종료일
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "business_member_auth", joinColumns = @JoinColumn(name = "business_member_no"))
    @Column(name = "auth") // 권한 내용이 들어갈 컬럼명
    @Builder.Default
    private Set<String> authSet = new HashSet<>();
    
    public void addRole(String role) {
        authSet.add(role);
    }
	
    public void changeStatus(int newStatus) {
        this.enabled = newStatus;
        LocalDateTime now = LocalDateTime.now(); // 변수로 고정

        switch (newStatus) {
            case 0: // 삭제
                this.dtdDate = now;
                break;
            case 1: // 활성화 (정지 해제)
                this.stopDate = null;
                this.stopEndDate = null;
                break;
            case 2: // 7일 정지
                this.stopDate = now;
                this.stopEndDate = now.plusDays(7);
                break;
            case 3: // 30일 정지
                this.stopDate = now;
                this.stopEndDate = now.plusDays(30);
                break;
            case 4: // 영구 정지
                this.stopDate = now;
                this.stopEndDate = now.plusYears(99);
                break;
        }
    }
    
    public void changePw(String pw) {
    	this.pw = pw;
    }
    
    public void setEnabled(int enabled) {
    	this.enabled = enabled;
    }
	
}
