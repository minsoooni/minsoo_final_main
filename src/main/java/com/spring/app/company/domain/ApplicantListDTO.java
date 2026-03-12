package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
public class ApplicantListDTO {
	private Long applicationId;        // 지원 ID
    private Long jobId;                // 공고 ID
    private String memberId;           // 지원자 회원 ID
    private Long submittedResumeId;    // 제출 이력서 ID

    private String name;               // 지원자명
    private String email;              // 이메일
    private String phone;              // 연락처
    private Date birthDate;            // 생년월일
    private Integer gender;            // 성별

    private Integer applicationRound;  // 지원 차수
    private Integer applicationStatus; // 지원 상태
    private Integer processStatus;     // 채용 진행 상태

    private Date appliedAt;            // 지원일
    private Date cancelledAt;          // 취소일
    private Date viewedAt;             // 열람일

    
    private String resumeTitle;        // 이력서 제목
    private Long desiredSalary;        // 희망 연봉

    private Integer careerYear;        // 경력 연차
    private String jobTitle;           // 공고 제목
}
