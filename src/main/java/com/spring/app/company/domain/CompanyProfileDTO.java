package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;


@Data
//기업 프로필용 DTO
public class CompanyProfileDTO {
	// =========================
    // TBL_COMPANY
    // =========================
    private String memberId;         // FK_MEMBERID
    private String companyName;      // COMPANY_NAME
    private String bizNo;            // BIZ_NO
    private String industryCode;     // INDUSTRY_CODE
    private String industryName;   // 업종코드를 한글 업종명으로 변환한 표시용 필드
    private String ceoName;          // CEO_NAME
    private Integer approvedYn;      // APPROVED_YN
    private Date approvalAt;         // APPROVAL_AT
    private String rejectReason;     // REJECT_REASON
    private Date infoCreatedAt;      // INFO_CREATED_AT
    private Date infoUpdatedAt;      // INFO_UPDATED_AT
    private String zipCode;          // ZIP_CODE
    private String addr1;            // ADDR1
    private String addr2;            // ADDR2
    private Double lat;              // LAT
    private Double lng;              // LNG

    // =========================
    // TBL_COMPANY_INTRO
    // =========================
    private Long companyIntroId;
    private String introText;        // INTRO_TEXT
    private String welfareText;      // WELFARE_TEXT
    private Date openDate;           // OPEN_DATE
    private String homepageUrl;      // HOMEPAGE_URL
    private String visionText;       // VISION_TEXT
    private String companyType;      // COMPANY_TYPE
    private Date createdAt;          // CREATED_AT
    private Date updatedAt;          // UPDATED_AT

    // =========================
    // 화면 표시용 추가 필드
    // =========================
    private String logoPath;         // 추후 파일테이블 연동 시 사용
}
