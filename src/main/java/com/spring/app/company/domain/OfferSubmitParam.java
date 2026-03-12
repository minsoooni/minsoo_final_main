package com.spring.app.company.domain;

import lombok.Data;

@Data
//발송 Request용 파라미터 DTO
public class OfferSubmitParam {
	private Long offerLetterId;
	private String expireAt;
	private Long offerSubmitId; // selectKey로 채워짐
	
}
