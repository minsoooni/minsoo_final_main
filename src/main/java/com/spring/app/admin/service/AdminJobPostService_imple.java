package com.spring.app.admin.service;

import com.spring.app.admin.domain.AdminJobPostDTO;
import com.spring.app.admin.model.AdminJobPostDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminJobPostService_imple implements AdminJobPostService {

    private final AdminJobPostDAO jobPostAdminDao;

    // 페이징 + 검색/필터
    @Override
    public List<AdminJobPostDTO> getPagedJobs(String search, String status, int page, int limit) {
        int offset = (page - 1) * limit;
        return jobPostAdminDao.selectPagedJobList(search, status, offset, limit);
    }

    // 전체 건수 (검색/필터 적용)
    @Override
    public int getJobCount(String search, String status) {
        return jobPostAdminDao.selectJobCount(search, status);
    }

    // 상태별 전체 건수 (통계 카드용)
    @Override
    public int getJobCountByStatus(String status) {
        return jobPostAdminDao.selectJobCountByStatus(status);
    }

    // 상태 변경
    
    @Transactional
    @Override
    public int updateJobHidden(Long jobId, Integer isHidden) {

        return jobPostAdminDao.updateJobHidden(jobId, isHidden);
    }

    @Override
    public int getJobCountExcludeClosedDeleted(String search) {
        return jobPostAdminDao.countJobsExcludeClosedDeleted(search);
    }
				
}