package com.spring.app.jobseeker.domain;

import java.util.List;

import lombok.Data;

/**
 * 구직자 화면에서 채용공고 목록/상세를 표시하기 위한 DTO
 * 
 * - 기업 파트의 JobPostingDTO(com.spring.app.company.domain)와 별도로 관리
 * - tbl_job_posting + 조인 테이블에서 구직자에게 보여줄 정보만 담음
 */
@Data
public class JobPostingListDTO {

    // === tbl_job_posting 기본 컬럼 === //
    private Long jobId;                // 공고 고유번호
    private String memberId;           // 기업 회원 ID
    private Long categoryId;           // 직무 카테고리 ID
    private String regionCode;         // 지역 코드
    private String title;              // 공고 제목
    private String content;            // 공고 내용 (CLOB, 상세에서 사용)
    private String workType;           // 근무 형태 (정규직/계약직/인턴…)
    private String careerType;         // 경력 구분 (신입/경력/무관)
    private String educationLevel;     // 학력 조건
    private Long salary;               // 급여
    private Long headcount;            // 채용인원
    private String status;             // 공고 상태 (임시저장/게시중/마감/삭제됨)
    private String deadlineType;       // 마감 방식 (date/always)
    private String deadlineAt;         // 마감일 (TO_CHAR 변환)
    private Long viewCount;            // 조회수
    private int isHidden;              // 숨김 상태 (0:활성, 1:숨김)
    private String openedAt;           // 게시 시작일
    private String closedAt;           // 게시 종료일
    private String createdAt;          // 생성일
    private String updatedAt;          // 수정일

    // === 조인으로 가져올 추가 정보 === //
    private String companyName;        // 회사명 (tbl_company)
    private String categoryName;       // 직무카테고리명 (tbl_job_category)
    private String regionName;         // 지역명 (tbl_region)
    private String parentRegionName;   // 상위지역명 - 시/도 (tbl_region)
    private String companyType;        // 기업형태 (tbl_company_intro)
    private String industryCode;       // 업종코드 (tbl_company)

    // === 기업 상세 정보 (사이드바용) === //
    private String ceoName;            // 대표자명 (tbl_company)
    private String bizNo;              // 사업자번호 (tbl_company)
    private String homepageUrl;        // 홈페이지 (tbl_company_intro)
    private String openDate;           // 설립일 (tbl_company_intro)
    private String companyAddr;        // 기업 주소 (tbl_company addr1+addr2)
    private String educationLevelName; // 학력명 (tbl_education_level.edu_level_name)
    private Long scrapCount;           // 스크랩수 (tbl_job_scrap COUNT)
    private Long applyCount;             // 지원자 수 (tbl_job_application COUNT)
    private String logoUrl;            // 기업 로고 URL (tbl_image_file)

    // === 기술스택 === //
    private String skillNames;         // 기술스택 콤마 구분 문자열 (LISTAGG)
    private List<String> skillList;    // 기술스택 리스트 (Service에서 변환)

    // === 화면 표시용 === //
    private String dDay;               // D-day 표시 문자열 (SQL에서 계산)
    private String dDayUrgent;          // D-day 긴급 여부 (Y/N)

    // === 매칭 점수 (추천/매칭도 표시용) === //
    private Integer matchScore;        // 매칭 총점 (0~100)
    private Integer regionScore;       // 지역 매칭 점수 (0/3/5)
    private Integer categoryScore;     // 직무 매칭 점수 (0/3)
    private Integer salaryScore;       // 연봉 매칭 점수 (0/2)
    private Integer techScore;         // 기술스택 매칭 점수 (일치수*2)
    private String matchedSkills;      // 일치 기술명 (콤마 구분, 예: "Java,Spring")
}
