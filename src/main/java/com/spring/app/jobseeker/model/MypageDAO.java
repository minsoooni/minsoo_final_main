package com.spring.app.jobseeker.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.jobseeker.domain.ResumeDTO;
import com.spring.app.member.domain.MemberDTO;

@Mapper
public interface MypageDAO {

    // 회원 기본정보 조회 (프로필용)
    MemberDTO selectMemberById(@Param("memberId") String memberId);

    // 대표이력서 조회 (사진, 희망지역, 희망직무)
    ResumeDTO selectPrimaryResume(@Param("memberId") String memberId);

    // 프로필 수정 (이름, 생년월일, 성별, 이메일, 전화번호)
    int updateProfile(MemberDTO dto);

    // 비밀번호 조회 (현재 비밀번호 확인용)
    String selectPassword(@Param("memberId") String memberId);

    // 비밀번호 수정
    int updatePassword(@Param("memberId") String memberId,
                       @Param("encodedPassword") String encodedPassword);

    // 커뮤니티 인증 직장명 등록/변경/취소 (취소 시 companyName = null)
    int updateCommunityCompanyName(@Param("memberId") String memberId,
                                   @Param("companyName") String companyName);

    // === 대시보드 통계 === //
    int selectTotalApplications(@Param("memberId") String memberId);
    int selectActiveApplications(@Param("memberId") String memberId);
    int selectTotalOffers(@Param("memberId") String memberId);
    int selectUnreadOffers(@Param("memberId") String memberId);
    int selectInterviewCount(@Param("memberId") String memberId);
    int selectScrappedCount(@Param("memberId") String memberId);
    int selectFollowedCompanies(@Param("memberId") String memberId);

    // === 대시보드 목록 === //
    List<Map<String, Object>> selectRecentApplications(@Param("memberId") String memberId);
    List<Map<String, Object>> selectRecentViewedPosts(@Param("memberId") String memberId);

    // === 캘린더 이벤트 === //
    List<Map<String, Object>> selectCalendarEvents(@Param("memberId") String memberId);
}
