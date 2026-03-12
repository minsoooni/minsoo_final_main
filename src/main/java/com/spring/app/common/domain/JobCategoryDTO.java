package com.spring.app.common.domain;

import lombok.Data;

@Data
public class JobCategoryDTO {
	private Long categoryId;
    private Long fkParentId;
    private String categoryName;
    private Integer depth;
}
