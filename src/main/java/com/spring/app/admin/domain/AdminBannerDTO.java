package com.spring.app.admin.domain;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AdminBannerDTO {
    private Long    bannerId;
    private String  fkMemberId;
    private Long    fkJobId;
    private String  title; 
    private Long    imageFileId;
    private LocalDate startAt;
    private LocalDate endAt;
    private int     priority;
    private String  status;       // ACTIVE / REJECTED / SUSPENDED (한글로 입력 예정)
    private String  companyName;  // JOIN으로 가져올 기업명
    private String  rejectReason; // 거절 사유 (별도 컬럼 or 임시 필드)
    private LocalDate createdAt;  // 신청일

    private String imageFileUrl;
}