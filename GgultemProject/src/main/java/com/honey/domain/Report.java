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
@Table(name = "REPORT")
@SequenceGenerator(name = "REPORT_SEQ_GEN", sequenceName = "REPORT_SEQ", allocationSize = 1)
@Getter
@ToString(exclude = {"reporter", "reportImage"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPORT_SEQ_GEN")
    @Column(name = "REPORT_ID")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_NO", nullable = false)
    private Member reporter; // 신고자

    @Column(name = "TARGET_MEMBER_ID", nullable = false)
    private String targetMemberId; // 신고 대상자 ID

    @Column(length = 100, nullable = false)
    private String reportType; // 신고 유형
    
    public static final String TYPE_ABUSE = "혐오/차별적/욕설 표현";
    public static final String TYPE_SPAM = "스팸홍보/도배";
    public static final String TYPE_EXPLICIT = "음란물/청소년에게 유해한 내용";
    public static final String TYPE_ILLEGAL = "불법적 내용";
    public static final String TYPE_FRAUD = "사기로 의심되는 내용";
    public static final String TYPE_ETC = "기타";

    @Column(length = 20, nullable = false)
    private String targetType; 
    
    public static final String TYPE_REPLY = "코멘트";
    public static final String TYPE_BOARD = "게시판";
    public static final String TYPE_CHAT = "채팅";

    @Column(length = 1500)
    private String reason; // 사유

    @Builder.Default
    @Column(nullable = false)
    private Integer status = 0; // 0: 접수, 1: 처리중, 2: 처리완료

    // 신고된 게시글이나 댓글의 번호
    @Column(name = "TARGET_NO")
    private Long targetNo; // 신고된 게시글이나 댓글의 번호

    // 증거 이미지 리스트 (Notice와 동일한 방식)
    @ElementCollection
    @Builder.Default
    private List<ReportImage> reportImage = new ArrayList<>();

    // 상태 변경 메서드
    public void changeStatus(Integer status) {
        this.status = status;
    }
    

    // 이미지 관리 메서드 (Notice 로직과 동일)
    public void addImage(ReportImage image) {
        image.setOrd(this.reportImage.size());
        reportImage.add(image);
    }

    public void addImageString(String fileName) {
        ReportImage image = ReportImage.builder().fileName(fileName).build();
        addImage(image);
    }

    public void clearList() {
        this.reportImage.clear();
    }
}

