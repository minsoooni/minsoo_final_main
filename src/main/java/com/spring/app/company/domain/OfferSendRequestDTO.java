package com.spring.app.company.domain;

import java.util.List;

import lombok.Data;


@Data
//작성된 제안서 발송용 DTO
public class OfferSendRequestDTO {
	private Long offerLetterId;
	private Long offerSubmitId;
    private String expireAt;
    private List<String> receiverMemberIds;
}
