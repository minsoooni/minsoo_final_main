package com.spring.app.company.domain;

import lombok.Data;



@Data
//배너 등록 DTO
public class BannerDTO {
	
	private Long bannerId;
    private String fkMemberId;
    private Long fkJobId;
    private String title;
    private Long imageFileId;
    private String startAt;
    private String endAt;
    private String status;
    private String rejectReason;
    
}
