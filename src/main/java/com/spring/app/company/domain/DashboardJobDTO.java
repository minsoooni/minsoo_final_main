package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
//대시보드용 채용공고 DTO
public class DashboardJobDTO {
	private Long jobId;           // 공고번호
    private String title;         // 공고 제목
    private String workTypeText;  // 근무형태
    private String status;        // 공고 상태 원본값
    private String statusText;    // 공고 상태 표시값
    private Date deadlineAt;      // 마감일
    private int viewCount;        // 조회수
    private int headcount;		  // 채용인원
    private int applicantCount;   // 지원자 수
    private int scrapCount;       // 스크랩 수
}
