package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class ResumeCareerDTO {

    private long careerId;             // 경력 ID (PK, seq_resume_career_id)
    private long resumeId;             // 이력서 ID (FK)
    private String companyname;        // 회사명
    private Long categoryId;           // 직무 분류 (FK -> tbl_job_category)
    private String jobnameCustom;      // 직무 직접입력
    private String position;           // 직책
    private String joindate;           // 입사일
    private String leavedate;          // 퇴사일 (재직중이면 NULL)
    private String jobDesc;            // 업무 내용
    private int sort;                  // 정렬 순서

    // === 조인 === //
    private String categoryName;       // 직무 카테고리명
}
