package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class ResumeEducationDTO {

    private long educationId;          // 학력 ID (PK, seq_resume_education_id)
    private long resumeId;             // 이력서 ID (FK)
    private String educationLevelCode; // 학력 수준 코드 (FK -> tbl_education_level)
    private String schoolname;         // 학교명
    private String major;              // 전공
    private String enrolldate;         // 입학일
    private String graduationdate;     // 졸업일
    private String status;             // 졸업상태 (졸업, 재학, 휴학 등)
    private int sort;                  // 정렬 순서

    // === 조인 === //
    private String educationLevelName; // 학력 수준명
}
