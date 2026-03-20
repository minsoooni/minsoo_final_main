package com.spring.app.admin.service;

import java.util.List;
import java.util.Map;

public interface AdminStatsService {

    // 총 회원 수 (탈퇴 제외)
    int getTotalMemberCount();

    // 구직자 수 (TYPE=0, 탈퇴 제외)
    int getJobSeekerCount();

    // 기업 회원 수 (TYPE=1, 탈퇴 제외)
    int getCompanyMemberCount();

    // 이번 달 신규 가입 수
    int getThisMonthJoinCount();

    // 이번 달 탈퇴 수
    int getThisMonthWithdrawCount();

    // 최근 30일 일별 신규 가입 추이
    List<Map<String, Object>> getDailyJoinLast30();

    // 최근 30일 일별 탈퇴 추이
    List<Map<String, Object>> getDailyWithdrawLast30();

    // ── 채용공고 통계 ──────────────────────────────
    int getActiveJobCount();
    int getThisMonthJobCount();
    int getThisMonthClosedJobCount();
    int getJobCountByStatus(int isHidden);
    List<Map<String, Object>> getDailyJobRegLast30();
    List<Map<String, Object>> getDailyJobClosedLast30();

    // ── 게시판 통계 ──────────────────────────────
    int getTotalPostCount();
    int getThisMonthPostCount();
    int getHiddenPostCount();
    List<Map<String, Object>> getDailyPostLast30();
    List<Map<String, Object>> getPostCountByBoard();
    int getInactiveBoardCount();

    // 도넛 그래프 분류
	int getClosedJobCount();

}
