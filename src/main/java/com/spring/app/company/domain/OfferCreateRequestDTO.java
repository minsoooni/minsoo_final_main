package com.spring.app.company.domain;

import lombok.Data;


@Data
//제안서 등록요청 DTO
public class OfferCreateRequestDTO {
	private Long offerLetterId; // selectKey로 채움
    private Long jobId;
    private String title;
    private String message;
}
