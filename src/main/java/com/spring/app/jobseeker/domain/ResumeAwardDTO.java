package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class ResumeAwardDTO {

    private long awardId;              // 수상 ID (PK, seq_resume_award_id)
    private long resumeId;             // 이력서 ID (FK)
    private String awardName;          // 수상명
    private String organizationName;   // 수여기관
    private String awardDate;          // 수상일
    private String description;        // 수상 내용
}
