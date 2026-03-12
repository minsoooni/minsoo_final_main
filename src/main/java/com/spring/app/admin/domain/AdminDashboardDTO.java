package com.spring.app.admin.domain;

import java.util.List;
import lombok.Data;

@Data
public class AdminDashboardDTO {

    // ───────────────────────────────
    // 상단 통계 카드
    // ───────────────────────────────
    private int totalMemberCount;       // 총 회원 수
    private int jobseekerCount;         // 구직자 수
    private int companyCount;           // 기업 수
    private int activeJobPostingCount;  // 활성 채용공고 수
    private int todayJobPostingCount;   // 오늘 등록 채용공고
    private int todayNewMemberCount;    // 오늘 신규 회원

    // ───────────────────────────────
    // 차트
    // ───────────────────────────────
    @Data
    public static class DailyMemberStatDTO {
        private String statDate;        // 날짜 (MM/DD)
        private int jobseekerCount;     // 구직자 가입 수
        private int companyCount;       // 기업 가입 수
    }


    // ───────────────────────────────
    // 기업 승인 대기
    // ───────────────────────────────
    @Data
    public static class PendingCompanyDTO {
        private String memberId;        // FK_MEMBERID
        private String companyName;     // 기업명
        private String infoCreatedAt;   // 신청일
    }

    // ───────────────────────────────
    // 최근 채용공고
    // ───────────────────────────────
    @Data
    public static class RecentJobPostingDTO {
        private String companyName;     // 기업명
        private String title;           // 공고 제목
        private String regionCode;      // 지역
        private String createdAt;       // 등록일시
        private String status;          // 상태
        private String deadlineAt;
    }

    @Data
    public static class DailyApplicationStatDTO {
        private String statDate;        // 날짜 (MM/DD)
        private int applicationCount;   // 지원 건수
    }

    private List<DailyApplicationStatDTO> dailyApplicationStats;
    
    // ───────────────────────────────
    // 최근 결제 내역
    // ───────────────────────────────
    @Data
    public static class RecentPaymentDTO {
        private String companyName;     // 기업명
        private long chargeAmount;      // 결제 금액
        private String paidAt;          // 결제일시
        private String status;          // 결제 상태
    }

   
    // ───────────────────────────────
    // List 필드
    // ───────────────────────────────
    private List<DailyMemberStatDTO> dailyMemberStats;
    private List<PendingCompanyDTO> pendingCompanies;
    private List<RecentJobPostingDTO> recentJobPostings;
    private List<RecentPaymentDTO> recentPayments;
    private int pendingJobReportCount;      // 채용공고 신고 대기 (TARGET_TYPE = 1)
    private int pendingPostReportCount;     // 게시글 신고 대기 (TARGET_TYPE = 2)
    private int pendingCommentReportCount;  // 댓글 신고 대기 (TARGET_TYPE = 3)
}


	
