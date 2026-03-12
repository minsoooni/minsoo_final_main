package com.spring.app.company.domain;

import lombok.Data;

@Data
//발송한 제안서 상태를 위한 DTO
public class OfferMetricsDTO {
	private Long offerLetterId;
    private Integer totalSent;
    private Integer viewedCnt;
    private Integer unreadCnt;
    private Integer acceptCnt;
    private Integer rejectCnt;
}
