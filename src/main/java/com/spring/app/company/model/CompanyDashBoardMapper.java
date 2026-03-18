package com.spring.app.company.model;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.company.domain.DashboardApplicantDTO;
import com.spring.app.company.domain.DashboardJobDTO;
import com.spring.app.company.domain.DashboardOfferDTO;

@Mapper
//채용공고 매퍼파일
public interface CompanyDashBoardMapper {

	// ===== KPI =====

    // 진행중인 공고 수
    int selectOngoingJobCount(@Param("memberId") String memberId);

    // 공고 상태별 갯수
    int selectJobWaitingCount(@Param("memberId") String memberId);
    int selectJobPostingCount(@Param("memberId") String memberId);
    int selectJobClosedCount(@Param("memberId") String memberId);
    
    // 총 지원자 수
    int selectTotalApplicantCount(@Param("memberId") String memberId);

    // 미확인 지원 수
    int selectUnreadApplicantCount(@Param("memberId") String memberId);

    // 지원자 상태별 인원수
    int selectApplicantUnreadCount(String memberId);
    int selectApplicantInterviewRequestCount(String memberId);
    
    
    // 발송된 제안서 수
    int selectSentOfferCount(@Param("memberId") String memberId);
    
    // 제안서 상태별 건수
    int selectOfferPendingCount(@Param("memberId") String memberId);
    int selectOfferAcceptedCount(@Param("memberId") String memberId);
    int selectOfferRejectedCount(@Param("memberId") String memberId);

    // 포인트 잔액
    Long selectPointBalance(@Param("memberId") String memberId);

    
    // 등록 배너 수
    int selectBannerCount(@Param("memberId") String memberId);
    
    // 배너 상태별 건수
    int selectBannerPendingCount(String memberId); //처리중 배너갯수
    int selectBannerApprovedCount(String memberId); //승인완료 배너갯수
    int selectBannerRejectedCount(String memberId); //반려 배너갯수
    

    // ===== 최근 목록 =====

    // 최근 채용 공고
    List<DashboardJobDTO> selectRecentJobs(@Param("memberId") String memberId);

    // 최근 지원자
    List<DashboardApplicantDTO> selectRecentApplicants(@Param("memberId") String memberId);

    // 최근 제안서
    List<DashboardOfferDTO> selectRecentOffers(@Param("memberId") String memberId);

   

}
