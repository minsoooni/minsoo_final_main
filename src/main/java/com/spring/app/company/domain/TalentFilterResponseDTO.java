package com.spring.app.company.domain;

import java.util.List;

import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.SkillCategoryDTO;
import com.spring.app.common.domain.SkillDTO;

import lombok.Data;

@Data
//인재 검색에서 REST API로 한 번에 내려줄 용도 DTO
public class TalentFilterResponseDTO {
	// 직무분야 목록
    private List<JobCategoryDTO> jobCategoryList;

    // 기술 카테고리 목록
    private List<SkillCategoryDTO> skillCategoryList;

    // 기술 목록
    private List<SkillDTO> skillList;
}
