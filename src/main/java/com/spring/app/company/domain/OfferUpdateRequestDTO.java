package com.spring.app.company.domain;

import lombok.Data;


@Data
//제안서 수정용 DTO
public class OfferUpdateRequestDTO {
	private Long offerLetterId;
    private Long jobId;
    private String title;
    private String message;
}
