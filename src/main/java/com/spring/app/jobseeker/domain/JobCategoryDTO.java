package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class JobCategoryDTO {

    private long categoryId;       // 직무 카테고리 ID
    private Long parentId;         // 상위 카테고리
    private String categoryName;   // 카테고리명
    private int depth;             // 계층 깊이 (1:대분류, 2:중분류)
    private Integer sortOrder;     // 정렬순서
}
