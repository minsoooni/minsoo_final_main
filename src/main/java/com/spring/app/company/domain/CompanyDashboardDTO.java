package com.spring.app.company.domain;

import java.util.List;

import lombok.Data;

@Data
//대시보드 전체 DTO
public class CompanyDashboardDTO {
	// ===== KPI =====
    private int ongoingJobCount;       // 진행중인 공고 수
    private int totalApplicantCount;   // 총 지원자 수
    private int unreadApplicantCount;  // 미확인 지원 수
    private int sentOfferCount;        // 발송된 제안서 수
    private long pointBalance;         // 포인트 잔액
    private int bannerCount;           // 등록된 배너 수

    // ===== 최근 목록 =====
    private List<DashboardJobDTO> recentJobs;
    private List<DashboardApplicantDTO> recentApplicants;
    private List<DashboardOfferDTO> recentOffers;
}
