package com.spring.app.common.domain;

import lombok.Data;

//기술스킬DTO
@Data
public class SkillDTO {
	private Long skillId;
    private Long fkSkillCategoryId;
    private String skillName;
}
