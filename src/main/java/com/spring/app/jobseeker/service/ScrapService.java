package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

public interface ScrapService {

    // 스크랩한 공고 목록 조회 (검색/정렬)
    List<Map<String, Object>> getScrappedPostList(Map<String, Object> paraMap);

    // 스크랩한 공고 총 건수
    int getScrappedPostCount(Map<String, Object> paraMap);

    // 스크랩 취소
    int removeScrap(String memberId, Long jobId);
}
