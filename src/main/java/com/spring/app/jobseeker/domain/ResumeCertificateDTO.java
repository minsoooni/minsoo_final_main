package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class ResumeCertificateDTO {

    private long certResumeId;         // PK (seq_resume_certificate_id)
    private long resumeId;             // 이력서 ID (FK)
    private Long certMasterId;         // 자격증 마스터 ID (FK -> tbl_certificate, NULL 가능)
    private String certName;           // 자격증명 (직접입력 or 마스터에서)
    private String certificateCode;    // 자격증 코드 (JS에서 전송용)
    private String certificateName;    // 자격증명 (조인)
    private String issuer;             // 발급기관 (조인)
    private String acquiredDate;       // 취득일
    private int sort;                  // 정렬 순서

    // === 조인 === //
    private String certMasterName;     // 자격증 마스터명
}
