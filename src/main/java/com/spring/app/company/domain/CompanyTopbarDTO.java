package com.spring.app.company.domain;

import lombok.Data;

@Data
//기업 상단바 DTO
public class CompanyTopbarDTO {
	private String memberId;
    private String companyName;
    private String email;
}
