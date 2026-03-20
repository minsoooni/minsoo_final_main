package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import com.spring.app.jobseeker.domain.ResumeDTO;
import com.spring.app.member.domain.MemberDTO;

public interface MypageService {

    // 회원 기본정보 조회
    MemberDTO getMemberInfo(String memberId);

    // 대표이력서 조회
    ResumeDTO getPrimaryResume(String memberId);

    // 프로필 수정
    int updateProfile(MemberDTO dto);

    // 비밀번호 조회 (현재 비밀번호 확인용)
    String getPassword(String memberId);

    // 비밀번호 수정
    int updatePassword(String memberId, String encodedPassword);

    // 커뮤니티 인증 직장명 등록/변경/취소
    int updateCommunityCompanyName(String memberId, String companyName);

    // 대시보드 통계
    Map<String, Integer> getDashboardStats(String memberId);

    // 최근 지원 내역 조회
    List<Map<String, Object>> getRecentApplications(String memberId);
    
    // 최근 본 공고 조회
    List<Map<String, Object>> getRecentViewedPosts(String memberId);

    // 캘린더 이벤트 조회
    List<Map<String, Object>> getCalendarEvents(String memberId);
}
