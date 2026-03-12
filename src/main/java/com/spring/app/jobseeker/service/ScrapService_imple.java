package com.spring.app.jobseeker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.model.JobPostingDAO;

@Service
public class ScrapService_imple implements ScrapService {

    private final JobPostingDAO jobPostingDAO;

    public ScrapService_imple(JobPostingDAO jobPostingDAO) {
        this.jobPostingDAO = jobPostingDAO;
    }

    // 스크랩한 공고 목록 조회
    @Override
    public List<Map<String, Object>> getScrappedPostList(Map<String, Object> paraMap) {
        return jobPostingDAO.selectScrappedPostList(paraMap);
    }

    // 스크랩한 공고 총 건수
    @Override
    public int getScrappedPostCount(Map<String, Object> paraMap) {
        return jobPostingDAO.selectScrappedPostCount(paraMap);
    }

    // 스크랩 취소
    @Transactional
    @Override
    public int removeScrap(String memberId, Long jobId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("jobId", jobId);
        return jobPostingDAO.deleteScrap(paraMap);
    }
}
