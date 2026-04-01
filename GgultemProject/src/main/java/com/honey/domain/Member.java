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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "member", uniqueConstraints = { @UniqueConstraint(name = "UK_MEMBER_ID", columnNames = "phone"),
		@UniqueConstraint(name = "UK_MEMBER_NICKNAME", columnNames = "nickname") })
@Getter
@ToString(exclude = "thumbnailList")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

	@Id
	private String email;
	private String pw;
	@Column(nullable = false)
	private String nickname;
	private boolean social;
	private String location;
	private String phone;
	
	//******* 비즈니스 회원 전용 조건 (일반 사용자는 null) **********//
	private String businessNumber; // 사업자 번호
    private String companyName;    // 상호명
    private boolean businessVerified; // 인증 여부
    private long bizMoney;
	
	private Integer enabled;
	private LocalDateTime dtdDate; // 삭제일
	private LocalDateTime stopDate; // 정지 시작일
	private LocalDateTime stopEndDate; // 정지 종료일

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "member_email")) // 테이블명 지정
	@Builder.Default
	private Set<MemberRole> memberRoleSet = new HashSet<>();

	@ElementCollection
	@Builder.Default
	private List<MemberThumbnail> thumbnailList = new ArrayList<>();
	
	public void upgradeToBusiness() {
        this.businessVerified = true;
        this.addRole(MemberRole.BUSINESS); // 권한도 즉시 추가
    }
	
	public void downgradeToBusiness() {
		this.businessVerified = false;
		clearRole();
        this.addRole(MemberRole.USER); // 권한 삭제 후 기본 권한 부여
	}
	
	public void chargeBizMoney(int amount) {
	    this.bizMoney += amount;
	}

	public void addRole(MemberRole memberRole) {
		memberRoleSet.add(memberRole);
	}

	public void clearRole() {
		memberRoleSet.clear();
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

	public void changePhone(String phone) {
		this.phone = phone;
	}

	public void changeNickName(String nickname) {
		this.nickname = nickname;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public void addImage(MemberThumbnail image) {
		image.setOrd(this.thumbnailList.size());
		thumbnailList.add(image);
	}

	public void addImageString(String fileName) {
		MemberThumbnail memberThumbnail = MemberThumbnail.builder().fileName(fileName).build();
		addImage(memberThumbnail);
	}

	public void clearList() {
		this.thumbnailList.clear();
	}

	public void changeBizMoney(long newBalance) {
		this.bizMoney = newBalance;
	}

	

}


