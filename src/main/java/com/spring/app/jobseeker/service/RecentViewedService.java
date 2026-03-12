package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

public interface RecentViewedService {

    // 최근본 공고 목록 조회 (페이징)
    List<Map<String, Object>> getRecentViewedPostList(Map<String, Object> paraMap);

    // 최근본 공고 총 건수
    int getRecentViewedPostCount(Map<String, Object> paraMap);

    // 최근본 공고 개별 삭제
    int removeViewedPost(String memberId, Long jobId);

    // 최근본 공고 전체 삭제
    int removeAllViewedPosts(String memberId);
}
