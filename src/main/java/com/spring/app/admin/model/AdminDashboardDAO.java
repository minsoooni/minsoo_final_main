package com.spring.app.admin.model;

import com.spring.app.admin.domain.AdminDashboardDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AdminDashboardDAO {

    // 상단 통계
    int getTotalMemberCount();
    int getJobseekerCount();
    int getCompanyCount();
    int getActiveJobPostingCount();
    int getTodayJobPostingCount();
    int getTodayNewMemberCount();

    // 차트
    List<AdminDashboardDTO.DailyMemberStatDTO> getDailyMemberStats();

    // 추가
    int getPendingReportCount(int targetType);
    
    // 알림: 기업 승인 대기
    List<AdminDashboardDTO.PendingCompanyDTO> getPendingCompanies();

    // 테이블
    List<AdminDashboardDTO.RecentJobPostingDTO> getRecentJobPostings();
    List<AdminDashboardDTO.RecentPaymentDTO> getRecentPayments();
    
    
    List<AdminDashboardDTO.DailyApplicationStatDTO> getDailyApplicationStats();
}
