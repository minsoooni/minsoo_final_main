package com.spring.app.jobseeker.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.common.domain.EducationDTO;
import com.spring.app.jobseeker.domain.JobCategoryDTO;
import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.domain.RegionDTO;

@Mapper
public interface JobPostingDAO {

    // 채용공고 전체 건수 조회 (검색/필터 조건 포함)
    int selectJobPostingCount(Map<String, Object> paraMap);

    // 채용공고 목록 조회 (검색/필터/페이징)
    List<JobPostingListDTO> selectJobPostingList(Map<String, Object> paraMap);

    // 추천 채용공고 조회 (대표이력서 기반: 희망지역, 희망직무, 희망연봉)
    List<JobPostingListDTO> selectRecommendedJobPostings(Map<String, Object> paraMap);

    // 인기 채용공고 조회 (조회수 높은순 n건)
    List<JobPostingListDTO> selectPopularJobPostings(@Param("limit") int limit);

    // 채용공고 상세 조회
    JobPostingListDTO selectJobPostingDetail(@Param("jobId") Long jobId);

    // 채용공고 조회수 1 증가
    int updateViewCount(@Param("jobId") Long jobId);

    // 공고별 기술스택 목록 조회
    List<String> selectSkillsByJobId(@Param("jobId") Long jobId);

    // === 마스터 데이터 조회 (필터용) === //

    // 지역 목록 조회
    List<RegionDTO> selectRegionList();

    // 학력 목록 조회
    List<EducationDTO> selectEduLevelList();

    // 직무 카테고리 목록 조회
    List<JobCategoryDTO> selectJobCategoryList();

    // 기술스택 목록 조회 (카테고리별)
    List<Map<String, Object>> selectSkillListGroupByCategory();

    // === 최근본 공고 === //

    // 공고 열람 기록 저장 (MERGE - 있으면 시간 갱신, 없으면 INSERT)
    int mergeViewLog(@Param("memberId") String memberId, @Param("jobId") Long jobId);

    // 최근본 공고 목록 조회 - 로그인 시 (최근 10건)
    List<JobPostingListDTO> selectRecentViewedJobs(@Param("memberId") String memberId);

    // 최근본 공고 목록 조회 - 비로그인 시 (쿠키의 공고 ID 목록으로 조회)
    List<JobPostingListDTO> selectJobPostingsByIds(@Param("jobIds") List<Long> jobIds);

    // === 지원 여부 확인 === //
    int checkAlreadyApplied(Map<String, Object> paraMap);

    // === 신고 관련 === //
    int insertReport(Map<String, Object> paraMap);
    int checkAlreadyReported(Map<String, Object> paraMap);
    List<Map<String, Object>> selectReportReasonList();

    // === 기업 회원 상태 === //
    int checkCompanyMemberStatus(@Param("companyMemberId") String companyMemberId);

    // === 팔로우 관련 === //
    int checkFollowStatus(Map<String, Object> paraMap);
    int insertFollow(Map<String, Object> paraMap);
    int deleteFollow(Map<String, Object> paraMap);

    // === 스크랩 관련 === //
    int checkScrapStatus(Map<String, Object> paraMap);
    int insertScrap(Map<String, Object> paraMap);
    int deleteScrap(Map<String, Object> paraMap);

    // 스크랩한 공고 목록 조회 (검색/정렬 포함)
    List<Map<String, Object>> selectScrappedPostList(Map<String, Object> paraMap);

    // 스크랩한 공고 총 건수
    int selectScrappedPostCount(Map<String, Object> paraMap);

    // === 지원자 통계 === //
    Map<String, Object> selectApplicantStats(@Param("jobId") Long jobId);
    Map<String, Object> selectApplicantAgeStats(@Param("jobId") Long jobId);
    Map<String, Object> selectApplicantSalaryStats(@Param("jobId") Long jobId);
    Map<String, Object> selectApplicantCertCountStats(@Param("jobId") Long jobId);
    Map<String, Object> selectApplicantEduStats(@Param("jobId") Long jobId);
    Map<String, Object> selectApplicantCareerStats(@Param("jobId") Long jobId);
    List<Map<String, Object>> selectApplicantTechTop5(@Param("jobId") Long jobId);
    List<Map<String, Object>> selectApplicantCertTop5(@Param("jobId") Long jobId);

    // === 최근본 공고 목록 (페이징) === //
    List<Map<String, Object>> selectRecentViewedPostList(Map<String, Object> paraMap);
    int selectRecentViewedPostCount(Map<String, Object> paraMap);

    // 최근본 공고 개별 삭제
    int deleteViewedPost(Map<String, Object> paraMap);

    // 최근본 공고 전체 삭제
    int deleteAllViewedPosts(@Param("memberId") String memberId);

    // === 매칭도 관련 === //

    // 채용상세 매칭도 조회 (특정 공고 1개 vs 내 이력서)
    Map<String, Object> selectMatchScoreForJob(Map<String, Object> paraMap);

    // 유사 공고 추천 (같은 직무/기술스택/지역 기준, 최대 4건)
    List<JobPostingListDTO> selectSimilarJobPostings(Map<String, Object> paraMap);
}
