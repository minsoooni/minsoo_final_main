package com.spring.app.company.domain;

import java.util.List;

import lombok.Data;

@Data
//대시보드 전체 DTO
public class CompanyDashboardDTO {
	// ===== KPI =====
    private int ongoingJobCount;       // 진행중인 공고 수
    
    // 공고 상태별 건수
    private int jobWaitingCount;   // 대기
    private int jobPostingCount;   // 게시중
    private int jobClosedCount;    // 마감
    
    
    private int totalApplicantCount;   // 총 지원자 수
    private int unreadApplicantCount;  // 미확인 지원 수
    
    private int sentOfferCount;        // 발송된 제안서 수
    
    // 제안서 상태별 건수
    private int offerPendingCount;   // 미응답
    private int offerAcceptedCount;  // 수락
    private int offerRejectedCount;  // 거절
    
    
    private long pointBalance;         // 포인트 잔액
    private int bannerCount;           // 등록된 배너 수

    // 지원자 상태별 건수
    private int applicantUnreadCount;             // 미열람
    private int applicantInterviewRequestCount;   // 면접요청
    
    
    // 배너 상태별 건수 추가
    private int bannerPendingCount;   // 처리중
    private int bannerApprovedCount;  // 승인완료
    private int bannerRejectedCount;  // 반려
    
    
    // ===== 최근 목록 =====
    private List<DashboardJobDTO> recentJobs;
    private List<DashboardApplicantDTO> recentApplicants;
    private List<DashboardOfferDTO> recentOffers;
}
