package com.spring.app.company.domain;

import java.time.LocalDateTime;

import lombok.Data;


@Data
//제안서 목록 조회용 DTO
public class OfferListDTO {
	private Long offerLetterId;
    private Long jobId;
    private String title;
    private LocalDateTime expireAt;     // 최신 발송(또는 대표 발송)의 만료일
    private Integer totalSent;
    private Integer viewedCnt;
    private Integer acceptCnt;
    private Integer rejectCnt;
    private Integer unreadCnt; // totalSent - viewedCnt
}
