package com.honey.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportImage {
    private String fileName;
    private int ord; // 이미지 순번

    public void setOrd(int ord) {
        this.ord = ord;
    }
}