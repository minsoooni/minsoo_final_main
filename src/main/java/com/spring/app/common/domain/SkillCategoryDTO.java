package com.spring.app.common.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


//기술스킬 카테고리 DTO
@Data
public class SkillCategoryDTO {
	
	private Long skillCategoryId;
    private String categoryName;

    private List<SkillDTO> skills = new ArrayList<>();
}
