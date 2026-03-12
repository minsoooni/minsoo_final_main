package com.spring.app.jobseeker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.model.JobPostingDAO;

@Service
public class RecentViewedService_imple implements RecentViewedService {

    private final JobPostingDAO jobPostingDAO;

    public RecentViewedService_imple(JobPostingDAO jobPostingDAO) {
        this.jobPostingDAO = jobPostingDAO;
    }

    // 최근본 공고 목록 조회
    @Override
    public List<Map<String, Object>> getRecentViewedPostList(Map<String, Object> paraMap) {
        return jobPostingDAO.selectRecentViewedPostList(paraMap);
    }

    // 최근본 공고 총 건수
    @Override
    public int getRecentViewedPostCount(Map<String, Object> paraMap) {
        return jobPostingDAO.selectRecentViewedPostCount(paraMap);
    }

    // 최근본 공고 개별 삭제
    @Transactional
    @Override
    public int removeViewedPost(String memberId, Long jobId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("jobId", jobId);
        return jobPostingDAO.deleteViewedPost(paraMap);
    }

    // 최근본 공고 전체 삭제
    @Transactional
    @Override
    public int removeAllViewedPosts(String memberId) {
        return jobPostingDAO.deleteAllViewedPosts(memberId);
    }
}
