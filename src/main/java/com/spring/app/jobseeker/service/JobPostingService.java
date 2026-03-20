package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import com.spring.app.common.domain.EducationDTO;
import com.spring.app.jobseeker.domain.JobCategoryDTO;
import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.domain.RegionDTO;

public interface JobPostingService {

    // 채용공고 전체 건수 조회 (검색/필터 조건 포함)
    int getJobPostingCount(Map<String, Object> paraMap);

    // 채용공고 목록 조회 (검색/필터/페이징)
    List<JobPostingListDTO> getJobPostingList(Map<String, Object> paraMap);

    // 추천 채용공고 조회 (대표이력서 기반)
    List<JobPostingListDTO> getRecommendedJobPostings(String memberId);

    // 인기 채용공고 조회 (조회수 높은순 n건)
    List<JobPostingListDTO> getPopularJobPostings(int limit);

    // 대표이력서 존재 여부 확인
    boolean hasPrimaryResume(String memberId);

    // 채용공고 상세 조회 (조회수 증가 포함)
    JobPostingListDTO getJobPostingDetail(Long jobId);

    // === 데이터 조회 (필터용) === //
    List<RegionDTO> getRegionList();
    List<EducationDTO> getEduLevelList();
    List<JobCategoryDTO> getJobCategoryList();
    List<Map<String, Object>> getSkillListGroupByCategory();

    // === 최근본 공고 === //
    void saveViewLog(String memberId, Long jobId);
    List<JobPostingListDTO> getRecentViewedJobs(String memberId);
    List<JobPostingListDTO> getJobPostingsByIds(List<Long> jobIds);

    // 지원 여부 확인
    boolean hasAlreadyApplied(Long jobId, String memberId);

    // 신고 관련
    List<Map<String, Object>> getReportReasonList();
    int submitReport(Long jobId, String memberId, Long reasonId, String reportContent);
    boolean hasAlreadyReported(Long jobId, String memberId);

    // 기업 회원 상태 조회
    int getCompanyMemberStatus(String companyMemberId);

    // 팔로우 관련
    boolean isFollowed(String companyMemberId, String memberId);
    void toggleFollow(String companyMemberId, String memberId, boolean follow);

    // 스크랩 관련
    boolean isScraped(Long jobId, String memberId);
    void toggleScrap(Long jobId, String memberId, boolean scrap);

    // 지원자 통계 조회
    Map<String, Object> getApplicantStats(Long jobId);

    // === 매칭도 관련 === //

    // 채용상세 매칭도 조회 (특정 공고와 내 대표이력서의 매칭도)
    Map<String, Object> getMatchScoreForJob(Long jobId, String memberId);

    // 유사 공고 추천 (같은 직무/기술스택/지역 기준, 최대 4건)
    List<JobPostingListDTO> getSimilarJobPostings(Long jobId);
}
