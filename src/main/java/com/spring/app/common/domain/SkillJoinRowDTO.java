package com.spring.app.common.domain;

import lombok.Data;


//조인 결과를 위한 스킬DTO
//조인 결과를 skillCatogoryDTO에 매핑하려면 mybatis가 복잡해져 DTO를 새로 만듦!
@Data
public class SkillJoinRowDTO {
	private Long skillCategoryId;
    private String categoryName;

    private Long skillId;
    private String skillName;
}
