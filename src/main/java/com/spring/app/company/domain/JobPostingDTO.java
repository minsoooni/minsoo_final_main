package com.spring.app.company.domain;

import java.time.LocalDateTime;
import java.util.Date;
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
    private String regionName;         // 추가: 현재 지역명
    private String parentRegionCode;   // 추가: 상위 지역코드
    private String parentRegionName;   // 추가: 상위 지역명
    
    
    private String title;          // 공고제목 (VARCHAR2(100))
    private String content;        // 공고내용 (CLOB)
    private String workType;       // 고용형태
    private String careerType;     // 경력구분
    
    private String eduCode; 	   // 학력수준
    private String eduLevelName;   // 학력이름
    private String educationLevelName; // 추가: 팝업 템플릿 호환용 별칭
    
    private Long salary;           // 최소연봉 (NUMBER(10))
    private Long headcount;        // 채용인원 (NUMBER(10))
    private String status;         // 상태[임시저장/진행중/마감]
    private String deadlineType;   // 마감구분[상시/마감일지정]
    private Long viewCount;        // 조회수
    private Long scrapCount;       // 스크랩수
    //스크랩 수는 매핑테이블에서 count(*) 를 이용해 인원수 조회해오기
    
    
    // 기업 정보(팝업 우측 카드용)
    private String companyName;
    private String companyType;   // 기업형태
    private String ceoName;       // 대표자
    private String industryCode;  // 업종명 또는 업종코드명
    private String openDate;      // 설립일
    private String companyAddr;   // 회사 주소
    private String homepageUrl;   // 홈페이지
    private String logoUrl;       // 기업 로고 경로
    
    
    // 기존 공고 필드 외 신고 관련 추가
    private Integer isHidden;              // 0: 정상, 1: 신고됨

    private Long reportId;
    private Long reportReasonId;
    private String reportReasonName;       // ★ TBL_REPORT_REASON.REASON_NAME
    private String reportContent;

    private String reportProcessStatus;    // DB 원본값
    private String reportProcessReason;

    private Date reportCreatedAt;
    private Date reportProcessedAt;

    private Integer reportCount;           // 목록용 신고 건수
    private String reportStatusText;       // 화면용 상태 문구
    
    
    private LocalDateTime deadlineAt;     // 마감일시
    private LocalDateTime openedAt;       // 공개게시일시
    private LocalDateTime closedAt;       // 공고마감일시
    private LocalDateTime createdAt;      // 등록일시
    private LocalDateTime updatedAt;    // 수정일시
    
    private List<Long> skillIds;
    private List<String> skillList;      // 기술스택 뱃지 출력용
}
