package com.spring.app.company.domain;

import java.util.List;

import lombok.Data;


@Data
//공고 수정용 DTO(기존의 값 끌어오기 위한 DTO)
public class JobPostingEditResponseDTO {
	private Long jobId;
    private String title;
    private Long categoryId;
    private Long parentCategoryId;

    private String regionCode;
    private String regionLv1;
    private String regionLv2;
    private String regionLv3;

    private List<Long> skillIds;
    
    
    // 추가: 폼에 채울 값들
    private String workType;
    private String careerType;
    private Long salary;
    private Integer headcount;
    private String eduCode;
    private String content;

    private String deadlineType;
    private String deadlineAt; // 또는 LocalDateTime (프론트 편하면 String 추천)
    private String openedAt;
    private String closedAt;

    private String status; // 임시저장/진행중/마감 등
}
