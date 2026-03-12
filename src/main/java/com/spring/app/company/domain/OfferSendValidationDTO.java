package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
//제안서 발송 전 공고/날짜 검사용 DTO
public class OfferSendValidationDTO {
	private Long offerLetterId;
    private Long jobId;
    private String companyMemberId;
    private String jobStatus;
    private Date openedAt;
    private Date deadlineAt;
    private Date closedAt;
}
