package com.honey.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "NOTICE")
@SequenceGenerator(name = "NOTICE_SEQ_GEN", sequenceName = "NOTICE_SEQ1",initialValue = 1,allocationSize = 1)
@Getter
@ToString(exclude = "member")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICE_SEQ_GEN")
    @Column(name = "NOTICE_ID")
    private Long noticeId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 3000, nullable = false)
    private String content;

    // Member 엔티티의 'no' 컬럼을 참조하는 외래키 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Integer enabled;
    
    private int viewCount;

    @Column(name = "DTD_DATE")
    private LocalDateTime dtdDate;

    @ElementCollection
	@Builder.Default 
	private List<NoticeImage> noticeImage = new ArrayList<>(); 

    
    //공지사항 제목 수정 메서드
    public void changeTitle(String title) {
        this.title = title;
    }

    //공지사항 내용 수정 메서드
    public void changeContent(String content) {
        this.content = content;
    }

    // 공지사항 상태 변경 (삭제 등)     
    public void changeStatus(int status) {
        this.enabled = status;
        if (status == 0) {
            this.dtdDate = LocalDateTime.now();
        }
    }
    
    public void changeViewCount(int viewCount) {
    	this.viewCount = viewCount;
    }
    
    public void setMember(Member member) {
    	this.member = member;
    }

    
    public void addImage(NoticeImage image) {
		image.setOrd(this.noticeImage.size());
		noticeImage.add(image);
	}

	public void addImageString(String fileName) {
		NoticeImage noticeImage = NoticeImage.builder().fileName(fileName).build();
		addImage(noticeImage);
	}

	public void clearList() {
		this.noticeImage.clear();
	}
}




//확인용 주석