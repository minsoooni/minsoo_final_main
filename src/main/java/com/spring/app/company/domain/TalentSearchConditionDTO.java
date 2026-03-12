package com.spring.app.company.domain;

import java.util.List;

import lombok.Data;

@Data
//검색 조건 DTO
public class TalentSearchConditionDTO {
	// 검색어 (이름, 이력서 제목, 직무)
    private String keyword;

    // 직무 카테고리 다중 선택
    private List<Long> jobCategoryIds;

    // 기술 스택 다중 선택
    private List<Long> skillIds;

    // 경력
    private Integer careerMin;
    private Integer careerMax;

    // 학력
    private String educationLevel;

    // 희망 근무지
    private String regionCode;

    // 희망 연봉
    private Long salaryMin;
    private Long salaryMax;

    // 최근 업데이트 기준
    private Integer updatedWithin;

    // 포트폴리오 보유 여부
    private Integer hasPortfolio;

    // 정렬
    private String sort;

    // 페이징
    private Integer page = 1;
    private Integer size = 10;

    public int getOffset() {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        return (p - 1) * s;
    }
    
    
}
