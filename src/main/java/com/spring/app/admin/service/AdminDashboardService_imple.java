package com.spring.app.admin.service;

import com.spring.app.admin.model.AdminDashboardDAO;
import com.spring.app.admin.domain.AdminDashboardDTO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService_imple implements AdminDashboardService {

    @Autowired
    private AdminDashboardDAO adminDashboardDAO;

    @Override
    public AdminDashboardDTO getDashboardData() {

        AdminDashboardDTO dto = new AdminDashboardDTO();

        // 상단 통계
        dto.setTotalMemberCount(adminDashboardDAO.getTotalMemberCount());
        dto.setJobseekerCount(adminDashboardDAO.getJobseekerCount());
        dto.setCompanyCount(adminDashboardDAO.getCompanyCount());
        dto.setActiveJobPostingCount(adminDashboardDAO.getActiveJobPostingCount());
        dto.setTodayJobPostingCount(adminDashboardDAO.getTodayJobPostingCount());
        dto.setTodayNewMemberCount(adminDashboardDAO.getTodayNewMemberCount());

        // 차트
        dto.setDailyMemberStats(adminDashboardDAO.getDailyMemberStats());

        // 신고
        dto.setPendingJobReportCount(adminDashboardDAO.getPendingReportCount(1));
        dto.setPendingPostReportCount(adminDashboardDAO.getPendingReportCount(2));
        dto.setPendingCommentReportCount(adminDashboardDAO.getPendingReportCount(3));
        
        // 알림
        dto.setPendingCompanies(adminDashboardDAO.getPendingCompanies());

        // 테이블
        dto.setRecentJobPostings(adminDashboardDAO.getRecentJobPostings());
        dto.setRecentPayments(adminDashboardDAO.getRecentPayments());

        
        dto.setDailyApplicationStats(adminDashboardDAO.getDailyApplicationStats());
        
        return dto;
    }

	
}
