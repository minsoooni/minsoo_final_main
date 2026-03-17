package com.spring.app.company.domain;

import java.time.LocalDateTime;

import lombok.Data;


@Data
//제안서 상세조회용 DTO
public class OfferDetailDTO {
	private Long offerLetterId;
	private String jobTitle; //공고 제목
    private Long jobId;
    private String title;
    private String message;
    private LocalDateTime expireAt;    // 최신 발송 기준
}
