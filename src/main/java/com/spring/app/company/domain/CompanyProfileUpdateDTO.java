package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
public class CompanyProfileUpdateDTO {
	private String memberId;      // 로그인 사용자 FK_MEMBERID

    // ===== TBL_COMPANY =====
    private String companyName;   // COMPANY_NAME
    private String bizNo;         // BIZ_NO
    private String industryCode;  // INDUSTRY_CODE
    private String ceoName;       // CEO_NAME
    private String zipCode;       // ZIP_CODE
    private String addr1;         // ADDR1
    private String addr2;         // ADDR2

    // ===== TBL_COMPANY_INTRO =====
    private String homepageUrl;   // HOMEPAGE_URL
    private String companyType;   // COMPANY_TYPE
    private String introText;     // INTRO_TEXT
    private String welfareText;   // WELFARE_TEXT
    private String visionText;    // VISION_TEXT

    
    // 설립연도(화면 입력값)
    // 예: 2015
    private String openYear;
    
    // DB 저장용 날짜값 (예: 2015-01-01)
    private Date openDate;
    
    // TBL_COMPANY_INTRO PK
    private Long companyIntroId;
}	
