package com.spring.app.company.domain;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
//채용공고 DTO
public class JobPostingDTO {
	
	private Long jobId;            // 공고ID (NUMBER(10))
    private String memberId;       // MEMBERID (VARCHAR2(50))
    private Long categoryId;       // 직무카테고리ID (NUMBER(10))
    private String categoryName;   // 목록/상세 화면 표시용 직무명
    private String regionCode;     // 지역코드 (VARCHAR2(30))
    private String title;          // 공고제목 (VARCHAR2(100))
    private String content;        // 공고내용 (CLOB)
    private String workType;       // 고용형태
    private String careerType;     // 경력구분
    private String eduCode; 	   // 학력수준
    private String eduLevelName;   // 학력이름
    private Long salary;           // 최소연봉 (NUMBER(10))
    private Long headcount;        // 채용인원 (NUMBER(10))
    private String status;         // 상태[임시저장/진행중/마감]
    private String deadlineType;   // 마감구분[상시/마감일지정]
    private Long viewCount;        // 조회수
    private Long scrapCount;       // 스크랩수
    //스크랩 수는 매핑테이블에서 count(*) 를 이용해 인원수 조회해오기
    
    private LocalDateTime deadlineAt;     // 마감일시
    private LocalDateTime openedAt;       // 공개게시일시
    private LocalDateTime closedAt;       // 공고마감일시
    private LocalDateTime createdAt;      // 등록일시
    private LocalDateTime updatedAt;    // 수정일시
    
    private List<Long> skillIds;
}
