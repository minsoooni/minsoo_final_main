package com.spring.app.admin.service;

import java.util.List;
import com.spring.app.admin.domain.AdminBannerDTO;

public interface AdminBannerService {
    // 배너 목록 조회 (페이징)
	List<AdminBannerDTO> getBannerList(int page, int limit, String status);
    // 전체 건수
	int getBannerCount(String status);
	// 전체 개수
	int getBannerTotalCount();	
    // 상태별 건수 (통계 카드용)
    int getBannerCountByStatus(String status);
    // 배너 승인
    int approveBanner(Long bannerId);
    // 배너 거절 (사유 포함)
    int rejectBanner(Long bannerId, String reason);
    // 배너 정지
    int stoppedBanner(Long bannerId);
    // 배너 재승인
    int unstoppedBanner(Long bannerId); 
    // 승인된 배너
    List<AdminBannerDTO> getActiveBanners();
    
}