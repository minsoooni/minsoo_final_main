package com.spring.app.jobseeker.domain;

import lombok.Data;

/**
 * 기업정보 목록/상세 표시용 DTO
 * 
 * tbl_company + tbl_company_intro + tbl_member + tbl_company_follow(팔로워수)
 * + tbl_job_posting(채용공고수) + tbl_image_file(로고) 조인
 */
@Data
public class CompanyInfoDTO {

    // === tbl_company === //
    private String memberId;           // 기업 회원 ID (fk_memberid)
    private String companyName;        // 회사명
    private String bizNo;              // 사업자번호
    private String industryCode;       // 업종 코드
    private String ceoName;            // 대표자명
    private String zipCode;            // 우편번호
    private String addr1;              // 주소1
    private String addr2;              // 주소2

    // === tbl_company_intro === //
    private String introText;          // 소개글 (CLOB)
    private String welfareText;        // 복지 정보 (CLOB)
    private String openDate;           // 설립일 (TO_CHAR 변환)
    private String foundedYear;        // 설립 연도 (화면표시용)
    private String homepageUrl;        // 홈페이지
    private String visionText;         // 비전 (CLOB)
    private String companyType;        // 기업형태 (대기업/중견기업/중소기업…)

    // === 조인으로 가져올 추가 정보 === //
    private String logoUrl;            // 로고 이미지 URL (tbl_image_file)
    private int followerCount;         // 팔로워 수 (tbl_company_follow COUNT)
    private int activePostCount;       // 진행중 채용공고 수 (tbl_job_posting COUNT)
    private int totalPostCount;        // 전체 공고 수

    // === 지역 표시용 === //
    private String regionName;         // 주소에서 추출한 간략 지역명 (화면 표시용)

    // === 팔로우 여부 (로그인 사용자 기준) === //
    private int isFollowed;            // 0: 미팔로우, 1: 팔로우중

    // === 로고 이니셜 (로고 이미지 없을 때 사용) === //
    public String getLogoInitial() {
        if (companyName != null && companyName.length() >= 2) {
            return companyName.substring(0, 2);
        }
        return companyName != null ? companyName : "??";
    }
}
