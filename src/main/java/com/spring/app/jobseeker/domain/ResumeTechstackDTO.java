package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class ResumeTechstackDTO {

    private long resumeId;             // 이력서 ID (PK, FK)
    private long skillId;              // 기술 ID (PK, FK)
    private int proficiency;           // 숙련도

    // === 조인 === //
    private String skillName;          // 기술명 (tbl_skill.skill_name)
    private String categoryName;       // 기술 카테고리명 (tbl_skill_category.category_name)
}
