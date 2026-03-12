package com.spring.app.jobseeker.domain;

import lombok.Data;

/**
 * 구직자가 받은 제안 목록/상세 표시용 DTO
 *
 * tbl_offer_response + tbl_offer_submit + tbl_offer_letter
 * + tbl_job_posting + tbl_company + tbl_company_intro + tbl_region 조인
 */
@Data
public class OfferReceivedDTO {

    // === tbl_offer_response === //
    private long offerSubmitId;         // 발송 제안서 ID (PK 일부)
    private String memberId;            // 구직자 회원 ID (PK 일부)
    private String viewedAt;            // 열람일시
    private int responseStatus;         // 응답 상태 (0:미응답, 1:수락, 2:거절)
    private String respondedAt;         // 응답일시

    // === tbl_offer_submit === //
    private String title;               // 제안서 제목
    private String message;             // 제안서 내용
    private String expireAt;            // 만료일 (응답 기한)
    private String sendAt;              // 발송일시

    // === tbl_offer_letter === //
    private long offerLetterId;         // 오퍼레터 ID

    // === tbl_job_posting === //
    private long jobId;                 // 공고 ID
    private String postTitle;           // 공고 제목
    private String workType;            // 근무형태 (정규직/계약직...)
    private String careerType;          // 경력구분 (신입/경력/무관)
    private Integer salary;             // 급여

    // === tbl_company === //
    private String companyMemberId;     // 기업 회원 ID
    private String companyName;         // 회사명
    private String ceoName;             // 대표자명
    private String industryCode;        // 업종코드
    private String addr1;               // 주소1
    private String addr2;               // 주소2

    // === tbl_company_intro === //
    private String companyType;         // 기업형태 (대기업/중견기업...)
    private String homepageUrl;         // 홈페이지
    private String openDate;            // 설립일

    // === tbl_region === //
    private String regionName;          // 지역명

    // === tbl_image_file === //
    private String companyLogo;         // 기업 로고 URL

    // === 화면 표시용 가공 필드 === //

    /**
     * 화면에 표시할 상태 문자열
     * viewedAt == null && responseStatus == 0 → "UNREAD"
     * viewedAt != null && responseStatus == 0 → "PENDING"
     * responseStatus == 1 → "ACCEPTED"
     * responseStatus == 2 → "REJECTED"
     */
    public String getStatus() {
        if (responseStatus == 1) return "ACCEPTED";
        if (responseStatus == 2) return "REJECTED";
        if (viewedAt == null)    return "UNREAD";
        return "PENDING";
    }

    /**
     * 급여 표시용 문자열
     */
    public String getSalaryText() {
        if (salary == null || salary == 0) return "협의";
        return String.format("%,d만원", salary);
    }
}
