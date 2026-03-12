package com.spring.app.company.domain;

import lombok.Data;

@Data
//발송한 제안서 전체 통계를 위한 DTO
public class OfferMetricsSummaryDTO {
	private Integer sent;     // 발송됨(총 발송 건수: response row count)
    private Integer viewed;   // 열람됨(viewed_at not null)
    private Integer accepted; // 수락(status=1)
    private Integer rejected; // 거절(status=2)
}
