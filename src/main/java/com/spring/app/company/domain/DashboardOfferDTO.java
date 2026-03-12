package com.spring.app.company.domain;

import java.util.Date;

import lombok.Data;

@Data
//대시보드용 제안서 DTO
public class DashboardOfferDTO {
	private Long offerLetterId;     // 제안서 번호
    private String title;           // 제안서 제목
    private String jobTitle;        // 연결된 공고 제목
    private int acceptedCount;      // 수락 수
    private int rejectedCount;      // 거절 수
    private int unreadCount;        // 미열람 수
    private int totalSentCount;     // 총 발송 수
    private Date expireAt;          // 만료일
}
