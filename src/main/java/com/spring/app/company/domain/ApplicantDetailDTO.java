package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ApplicantDetailDTO {

    // ================================
    // tbl_job_application 기본 정보
    // ================================
    private Long applicationId;          // 지원 ID
    private Long jobId;                  // 공고 ID
    private String memberId;             // 지원자 회원 ID
    private Long submittedResumeId;      // 제출 이력서 ID

    private Integer applicationRound;    // 지원 차수
    private Integer applicationStatus;   // 지원 상태 (0: 제출, 1: 취소)
    private Integer processStatus;       // 진행 상태 (0: 미열람, 1: 열람, 2: 서류탈락, 3: 면접요청, 4: 합격, 5: 불합격)
    private String processStatusText;    // 진행 상태 한글 텍스트

    private Date appliedAt;              // 지원일
    private Date cancelledAt;            // 지원 취소일
    private Date viewedAt;               // 열람일

    // ================================
    // 공고 정보 (tbl_job_posting + 회사/지역/직무 조인)
    // ================================
    private String postTitle;            // 공고 제목
    private String companyName;          // 회사명
    private String regionName;           // 지역명
    private String categoryName;         // 직무 카테고리명
    private String workType;             // 근무 형태
    private String salary;               // 공고에 등록된 급여
    private Date deadlineAt;             // 공고 마감일

    // ================================
    // tbl_job_application 스냅샷
    // ================================
    private String name;                 // 지원자 이름
    private Date birthDate;              // 생년월일
    private Integer gender;              // 성별 (1: 남, 2: 여)
    private String phone;                // 연락처
    private String email;                // 이메일

    // ================================
    // tbl_submitted_resume 스냅샷
    // ================================
    private String resumeTitle;          // 제출 이력서 제목
    private String selfIntro;            // 자기소개
    private String education;            // 학력 JSON 문자열
    private String career;               // 경력 JSON 문자열
    private String language;             // 어학 JSON 문자열
    private String portfolio;            // 포트폴리오 JSON 문자열
    private String award;                // 수상 JSON 문자열
    private String address;              // 주소
    private String photoPath;            // 증명사진 경로
    private Long desiredSalary;          // 희망 연봉
}