package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import com.spring.app.jobseeker.domain.CompanyInfoDTO;

public interface FollowCompanyService {

    // 팔로우한 기업 목록 조회
    List<CompanyInfoDTO> getFollowedCompanyList(Map<String, Object> paraMap);

    // 팔로우한 기업 총 건수
    int getFollowedCompanyCount(Map<String, Object> paraMap);

    // 팔로우 해제
    int removeFollow(String memberId, String companyMemberId);
}
