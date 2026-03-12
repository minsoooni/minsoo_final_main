package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
//제안서 수신자 상세 확인을 위한 DTO
public class OfferRecipientDetailDTO {
	private Long offerLetterId;
    private Long offerSubmitId;

    private String offerTitle;

    private String memberId;
    private String receiverName;
    private String receiverEmail;

    private Date sendAt;
    private Date viewedAt;
    private Date respondedAt;

    private Integer responseStatus;
    private String responseStatusText;
}
