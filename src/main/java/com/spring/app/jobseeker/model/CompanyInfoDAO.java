package com.spring.app.jobseeker.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.jobseeker.domain.CompanyInfoDTO;
import com.spring.app.jobseeker.domain.JobPostingListDTO;

@Mapper
public interface CompanyInfoDAO {

    // 기업 목록 조회 (검색/필터/정렬/페이징)
    List<CompanyInfoDTO> selectCompanyList(Map<String, Object> paraMap);

    // 기업 목록 총 건수
    int selectCompanyListTotalCount(Map<String, Object> paraMap);

    // 기업 상세 조회
    CompanyInfoDTO selectCompanyDetail(@Param("memberId") String memberId,
                                       @Param("loginMemberId") String loginMemberId);

    // 해당 기업의 진행중 채용공고 목록
    List<JobPostingListDTO> selectCompanyJobPostings(@Param("memberId") String memberId);

    // 해당 기업의 진행중 채용공고 목록 (페이징)
    List<JobPostingListDTO> selectCompanyJobPostingsPaged(Map<String, Object> paraMap);

    // 해당 기업의 진행중 채용공고 총 건수
    int selectCompanyJobPostingsCount(@Param("memberId") String memberId);

    // 유사 기업 목록 (같은 업종)
    List<CompanyInfoDTO> selectSimilarCompanies(@Param("memberId") String memberId,
                                                 @Param("industryCode") String industryCode);

    // 팔로우 여부 확인
    int selectFollowStatus(@Param("companyMemberId") String companyMemberId,
                           @Param("memberId") String memberId);

    // 팔로우 등록
    int insertFollow(@Param("companyMemberId") String companyMemberId,
                     @Param("memberId") String memberId);

    // 팔로우 해제
    int deleteFollow(@Param("companyMemberId") String companyMemberId,
                     @Param("memberId") String memberId);

    // 팔로워 수 조회
    int selectFollowerCount(@Param("companyMemberId") String companyMemberId);

    // 팔로우한 기업 목록 조회 (관심기업 페이지용)
    List<CompanyInfoDTO> selectFollowedCompanyList(Map<String, Object> paraMap);

    // 팔로우한 기업 총 건수
    int selectFollowedCompanyCount(Map<String, Object> paraMap);
}
