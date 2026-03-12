package com.spring.app.company.domain;

import java.util.List;

import lombok.Data;

@Data
//기술스택 - 채용공고 매핑테이블 변경을 위한 DTO
public class JobPostingUpdateRequestDTO {
	
	private JobPostingDTO job;
    private List<Long> skillIds;
}
