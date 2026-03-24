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
     * responseStatus == 1 → "ACCEPTED"
     * responseStatus == 2 → "REJECTED"
     * 만료일 지남 && responseStatus == 0 → "EXPIRED"
     * viewedAt == null && responseStatus == 0 → "UNREAD"
     * viewedAt != null && responseStatus == 0 → "PENDING"
     */
    public String getStatus() {
        if (responseStatus == 1) return "ACCEPTED"; // 수락시 ACCEPTED(수락)
        if (responseStatus == 2) return "REJECTED"; // 거절시 REJECTED(거절)
        
        if (responseStatus == 0 && expireAt != null && !expireAt.isEmpty()) {// 만료일 체크: 미응답(0) 상태에서 만료일이 지났으면 EXPIRED(기간만료)
            try {
                java.time.LocalDate expire = java.time.LocalDate.parse(expireAt.substring(0, 10),
                        java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                if (expire.isBefore(java.time.LocalDate.now())) return "EXPIRED";
            } catch (Exception e) { /* 실패 시 무시 */ }
        }
        if (viewedAt == null) return "UNREAD"; // 미응답 + 만료 안 됨 + 열람 안 함 → UNREAD(미열람)
        return "PENDING"; // 그외 나머지 PENDING(검토중)
    }

    /**
     * 급여 표시용 문자열
     */
    public String getSalaryText() {
        if (salary == null || salary == 0) return "협의";
        return String.format("%,d만원", salary);
    }

    /**
     * 신규 제안 여부 (발송일이 24시간 이내 + 미응답 상태만)
     */
    public boolean isNew() {
        if (!"UNREAD".equals(getStatus())) return false;
        if (sendAt == null || sendAt.isEmpty()) return false;
        try {
            java.time.LocalDateTime sent = java.time.LocalDateTime.parse(sendAt,
                    java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
            return sent.isAfter(java.time.LocalDateTime.now().minusHours(24));
        } catch (Exception e) {
            return false;
        }
    }
}
