package com.spring.app.admin.service;

import java.util.List;
import com.spring.app.admin.domain.AdminJobPostDTO;

public interface AdminJobPostService {

    // 페이징 + 검색/필터 추가
    List<AdminJobPostDTO> getPagedJobs(String search, String status, int page, int limit);

    // 전체 건수 (검색/필터 적용)
    int getJobCount(String search, String status);

    // 상태별 전체 건수 (통계 카드용)
    int getJobCountByStatus(String status);

    // 상태 변경
	int updateJobHidden(Long jobId, Integer isHidden);

	int getJobCountExcludeClosedDeleted(String search);
	
}