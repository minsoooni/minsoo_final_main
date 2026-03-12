package com.spring.app.jobseeker.domain;

import lombok.Data;

/**
 * tbl_submitted_resume : 지원 시점 이력서 스냅샷 DTO
 */
@Data
public class SubmittedResumeDTO {

    private long submittedResumeId;     // 지원서 ID (PK, seq_submitted_resume_id)
    private String memberid;            // 작성자 회원 ID
    private String title;               // 지원서 제목
    private String address;             // 주소
    private String selfIntro;           // 자기소개
    private String photoPath;           // 사진 경로
    private String education;           // 학력 내용 (JSON 스냅샷)
    private String career;              // 경력 내용 (JSON 스냅샷)
    private String language;            // 어학 (JSON 스냅샷)
    private String portfolio;           // 포트폴리오 (JSON 스냅샷)
    private String award;               // 수상 (JSON 스냅샷)
    private String submittedAt;         // 제출일
    private String eduLevelCode;        // 최종 학력 코드
    private int totalCareerMonths;      // 총 경력 개월수
    private Long desiredSalary;         // 희망 연봉
}
