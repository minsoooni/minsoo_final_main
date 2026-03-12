package com.spring.app.company.domain;

import lombok.Data;

@Data
public class CompanyProfileUpdateResponseDTO {
	private boolean success;
    private String message;
    private String logoPath;   // 업로드 후 미리보기 갱신용
}
