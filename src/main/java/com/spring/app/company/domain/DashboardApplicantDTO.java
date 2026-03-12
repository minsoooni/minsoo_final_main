package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
//대시보드용 지원자 DTO
public class DashboardApplicantDTO {
	private Long applicationId;        // 지원번호
    private String applicantName;      // 지원자 이름
    private String resumeTitle;        // 제출 이력서 제목
    private String jobTitle;           // 지원 공고명
    private Date appliedAt;            // 지원일
    private Integer processStatus;     // 전형 상태 코드
    private String processStatusText;  // 전형 상태 텍스트
}
