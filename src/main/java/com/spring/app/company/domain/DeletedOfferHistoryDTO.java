package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
//삭제된 제안서에 대해 발송한 제안서 내역 조회 DTO
public class DeletedOfferHistoryDTO {
	private Long offerLetterId;
    private Long jobId;
    private String title;

    private Integer totalSent;
    private Integer viewedCnt;
    private Integer unreadCnt;
    private Integer acceptCnt;
    private Integer rejectCnt;

    private Date lastSendAt;
}
