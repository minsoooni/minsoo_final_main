package com.spring.app.admin.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminStatsDAO {
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
    // 활성 채용공고 수 (STATUS = 'ACTIVE', IS_HIDDEN = 0)
    int getActiveJobCount();

    // 이번 달 신규 등록 수
    int getThisMonthJobCount();

    // 이번 달 마감 수 (CLOSED_AT 기준)
    int getThisMonthClosedJobCount();

    // 상태별 공고 수 (STATUS 값으로 조회)
    int getJobCountByStatus(int status);

    // 최근 30일 일별 등록 추이
    List<Map<String, Object>> getDailyJobRegLast30();

    // 최근 30일 일별 마감 추이
    List<Map<String, Object>> getDailyJobClosedLast30();

    // ── 게시판 통계 ──────────────────────────────
    // 전체 게시글 수 (삭제/숨김 제외)
    int getTotalPostCount();

    // 이번 달 신규 게시글 수
    int getThisMonthPostCount();

    // 숨김 처리된 게시글 수
    int getHiddenPostCount();

    // 최근 30일 일별 게시글 작성 추이
    List<Map<String, Object>> getDailyPostLast30();

    // 게시판별 게시글 수 (차트용 보기)
    List<Map<String, Object>> getPostCountByBoard();

    // 게시판별 게시글 수 (FK_BOARD_ID 기준)
    int getInactiveBoardCount();

    // 도넛 그래프 분류
	int getClosedJobCount();
   


        


}
