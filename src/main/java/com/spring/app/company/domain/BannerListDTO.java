package com.spring.app.company.domain;

import lombok.Data;

@Data
//배너리스트용 DTO
public class BannerListDTO {
	private Long bannerId;
    private String fkMemberId;
    private Long fkJobId;
    private String title;
    private Long imageFileId;
    private String startAt;
    private String endAt;
    private String status;
    private String rejectReason;

    
    // 조인으로 가져올 값
    private String jobTitle;             // 연결된 공고 제목
    private String fileUrl;             // 이미지 URL
    private String originalFilename;    // 원본 파일명
}
