package com.spring.app.jobseeker.domain;

import lombok.Data;

/**
 * tbl_job_application : 입사지원 DTO
 */
@Data
public class JobApplicationDTO {

    private long applicationId;         // 지원 ID (PK, seq_job_application_id)
    private long jobId;                 // 공고 ID (FK)
    private String memberid;            // 지원자 회원 ID (FK)
    private long submittedResumeId;     // 지원서 ID (FK)
    private String name;                // 이름 (스냅샷)
    private String birthDate;           // 생년월일 (스냅샷)
    private int gender;                 // 성별 1:남, 2:여 (스냅샷)
    private String phone;               // 연락처 (스냅샷)
    private String email;               // 이메일 (스냅샷)
    private int applicationRound;       // 지원 차수
    private int applicationStatus;      // 지원 상태 (0:제출, 1:취소)
    private int processStatus;          // 진행 상태 (0:미열람, 1:열람, 2:서류탈락, 3:면접요청, 4:합격, 5:불합격)
    private String appliedAt;           // 지원일
    private String cancelledAt;         // 취소일
    private String viewedAt;            // 열람일

    // === 조인 정보 (목록/상세 조회 시 사용) === //
    private String postTitle;           // 공고 제목
    private String companyName;         // 회사명
    private String resumeTitle;         // 지원서 제목
    private String processStatusText;   // 진행 상태 텍스트
    private String regionName;          // 지역명
    private String categoryName;        // 직무명
    private String workType;            // 근무형태
    private Long salary;                // 급여
    private String deadlineAt;          // 마감일
    private String companyLogo;         // 기업 로고 URL (tbl_image_file)
}
