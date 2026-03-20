package com.spring.app.admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.app.admin.model.AdminStatsDAO;

@Service
public class AdminStatsService_imple implements AdminStatsService {

    @Autowired
    private AdminStatsDAO adminStatsDAO;

    @Override
    public int getTotalMemberCount() {
        return adminStatsDAO.getTotalMemberCount();
    }

    @Override
    public int getJobSeekerCount() {
        return adminStatsDAO.getJobSeekerCount();
    }

    @Override
    public int getCompanyMemberCount() {
        return adminStatsDAO.getCompanyMemberCount();
    }

    @Override
    public int getThisMonthJoinCount() {
        return adminStatsDAO.getThisMonthJoinCount();
    }

    @Override
    public int getThisMonthWithdrawCount() {
        return adminStatsDAO.getThisMonthWithdrawCount();
    }

    @Override
    public List<Map<String, Object>> getDailyJoinLast30() {
        return adminStatsDAO.getDailyJoinLast30();
    }

    @Override
    public List<Map<String, Object>> getDailyWithdrawLast30() {
        return adminStatsDAO.getDailyWithdrawLast30();
    }

    // ── 채용공고 통계 ──────────────────────────────
    @Override
    public int getActiveJobCount() {
        return adminStatsDAO.getActiveJobCount();
    }

    @Override
    public int getThisMonthJobCount() {
        return adminStatsDAO.getThisMonthJobCount();
    }

    @Override
    public int getThisMonthClosedJobCount() {
        return adminStatsDAO.getThisMonthClosedJobCount();
    }

 // 수정
    @Override
    public int getJobCountByStatus(int isHidden) {
        return adminStatsDAO.getJobCountByStatus(isHidden);
    }

    @Override
    public List<Map<String, Object>> getDailyJobRegLast30() {
        return adminStatsDAO.getDailyJobRegLast30();
    }

    @Override
    public List<Map<String, Object>> getDailyJobClosedLast30() {
        return adminStatsDAO.getDailyJobClosedLast30();
    }

    // ── 게시판 통계 ──────────────────────────────
    @Override
    public int getTotalPostCount() {
        return adminStatsDAO.getTotalPostCount();
    }

    @Override
    public int getThisMonthPostCount() {
        return adminStatsDAO.getThisMonthPostCount();
    }

    @Override
    public int getHiddenPostCount() {
        return adminStatsDAO.getHiddenPostCount();
    }

    @Override
    public List<Map<String, Object>> getDailyPostLast30() {
        return adminStatsDAO.getDailyPostLast30();
    }

    @Override
    public List<Map<String, Object>> getPostCountByBoard() {
        return adminStatsDAO.getPostCountByBoard();
    }    
    
    @Override
    public int getInactiveBoardCount() {
        return adminStatsDAO.getInactiveBoardCount();
    }

	@Override
	public int getClosedJobCount() {
	    return adminStatsDAO.getClosedJobCount();
	}
   
}
