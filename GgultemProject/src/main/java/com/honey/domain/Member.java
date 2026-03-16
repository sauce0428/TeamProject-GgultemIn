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
@Table(name = "member", uniqueConstraints = {
	    @UniqueConstraint(name = "UK_MEMBER_ID", columnNames = "ID"),
	    @UniqueConstraint(name = "UK_MEMBER_RRN", columnNames = "RRN"),
	    @UniqueConstraint(name = "UK_MEMBER_NICKNAME", columnNames = "NICK_NAME")
	})
@SequenceGenerator(name = "MEMBER_SEQ_GEN",
		sequenceName = "MEMBER_SEQ1",
		initialValue = 1,
		allocationSize = 1)
@Getter
@ToString(exclude = "thumbnailList")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GEN")
	private Long no;
	
	@Column(nullable = false)
	private String id;
	
	private String pw;
	
	@Column(nullable = false)
	private String rrn;
	
	@Column(nullable = false)
	private String nickName;
	
	private String location;
	private String phone;
	private String email;
	private Integer enabled;
	private LocalDateTime dtdDate;   // 삭제일
    private LocalDateTime stopDate;  // 정지 시작일
    private LocalDateTime stopEndDate;  // 정지 종료일
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_auth", joinColumns = @JoinColumn(name = "member_no"))
    @Column(name = "auth") // 권한 내용이 들어갈 컬럼명
    @Builder.Default
    private Set<String> authSet = new HashSet<>();
    
    @ElementCollection 
	@Builder.Default 
	private List<MemberThumbnail> thumbnailList = new ArrayList<>(); 

    public void addRole(String role) {
    	if (this.authSet == null) {
            this.authSet = new HashSet<>();
        }
        this.authSet.add(role);
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
    public void changeEmail(String email) {
    	this.email = email;
    }
    public void changeNickName(String nickName) {
    	this.nickName = nickName;
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
	
}
