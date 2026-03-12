package com.spring.app.company.domain;

import lombok.Data;

@Data
//제안서-응답 매핑 DTO
public class OfferResponseDTO {
	private Long offerLetterId;   // 제안서 ID (FK)
    private String memberId;      // 구직자 ID (FK)

    private String viewedAt;        // 열람일
    private Integer responseStatus; // 응답상태 (0:대기, 1:수락, 2:거절)
    private String respondedAt;     // 응답일
}
