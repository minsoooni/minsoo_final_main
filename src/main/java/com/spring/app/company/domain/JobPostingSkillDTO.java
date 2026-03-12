package com.spring.app.company.domain;

import lombok.Data;

@Data
//채용공고-기술스택 매핑 DTO
public class JobPostingSkillDTO {
	private Long jobId;    // 공고ID
    private Long skillId;  // 기술스택ID
}
