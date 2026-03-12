package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import com.spring.app.jobseeker.domain.CompanyInfoDTO;
import com.spring.app.jobseeker.domain.JobPostingListDTO;

public interface CompanyInfoService {

    // 기업 목록 조회
    List<CompanyInfoDTO> getCompanyList(Map<String, Object> paraMap);

    // 기업 목록 총 건수
    int getCompanyListTotalCount(Map<String, Object> paraMap);

    // 기업 상세 조회
    CompanyInfoDTO getCompanyDetail(String memberId, String loginMemberId);

    // 해당 기업의 진행중 채용공고 목록
    List<JobPostingListDTO> getCompanyJobPostings(String memberId);

    // 해당 기업의 진행중 채용공고 목록 (페이징)
    List<JobPostingListDTO> getCompanyJobPostingsPaged(Map<String, Object> paraMap);

    // 해당 기업의 진행중 채용공고 총 건수
    int getCompanyJobPostingsCount(String memberId);

    // 유사 기업 목록
    List<CompanyInfoDTO> getSimilarCompanies(String memberId, String industryCode);

    // 팔로우 토글 (팔로우/언팔로우) - 변경 후 팔로워 수 반환
    Map<String, Object> toggleFollow(String companyMemberId, String memberId);
}
