package com.spring.app.jobseeker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.domain.ResumeDTO;
import com.spring.app.jobseeker.model.MypageDAO;
import com.spring.app.member.domain.MemberDTO;

@Service
public class MypageService_imple implements MypageService {

    private final MypageDAO mypageDAO;

    public MypageService_imple(MypageDAO mypageDAO) {
        this.mypageDAO = mypageDAO;
    }

    // 회원 정보 조회
    @Override
    public MemberDTO getMemberInfo(String memberId) {
        return mypageDAO.selectMemberById(memberId);
    }

    // 대표이력서 조회
    @Override
    public ResumeDTO getPrimaryResume(String memberId) {
        return mypageDAO.selectPrimaryResume(memberId);
    }

    // 프로필 수정
    @Transactional
    @Override
    public int updateProfile(MemberDTO dto) {
        return mypageDAO.updateProfile(dto);
    }

    // 비밀번호 조회
    @Override
    public String getPassword(String memberId) {
        return mypageDAO.selectPassword(memberId);
    }

    // 비밀번호 수정
    @Transactional
    @Override
    public int updatePassword(String memberId, String encodedPassword) {
        return mypageDAO.updatePassword(memberId, encodedPassword);
    }

    // 커뮤니티 인증 직장명 등록/변경/취소
    @Transactional
    @Override
    public int updateCommunityCompanyName(String memberId, String companyName) {
        return mypageDAO.updateCommunityCompanyName(memberId, companyName);
    }

    // 대시보드 통계
    @Override
    public Map<String, Integer> getDashboardStats(String memberId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalApplications", mypageDAO.selectActiveApplications(memberId));    // 진행중 지원 수 (큰 숫자)
        stats.put("activeApplications", mypageDAO.selectInterviewCount(memberId));       // 면접요청 수 (하단)
        stats.put("totalOffers", mypageDAO.selectTotalOffers(memberId));                 // 대응 필요 제안 수 (큰 숫자)
        stats.put("unreadOffers", mypageDAO.selectUnreadOffers(memberId));               // 미열람 제안 수 (하단)
        stats.put("scrappedPosts", mypageDAO.selectScrappedCount(memberId));
        stats.put("followedCompanies", mypageDAO.selectFollowedCompanies(memberId));
        return stats;
    }

    // 최근 지원 내역 조회
    @Override
    public List<Map<String, Object>> getRecentApplications(String memberId) {
        return mypageDAO.selectRecentApplications(memberId);
    }

    // 최근 본 공고 조회
    @Override
    public List<Map<String, Object>> getRecentViewedPosts(String memberId) {
        return mypageDAO.selectRecentViewedPosts(memberId);
    }

    /** 캘린더 이벤트 조회 (지원일/마감일/제안 응답기한) */
    @Override
    public List<Map<String, Object>> getCalendarEvents(String memberId) {
        return mypageDAO.selectCalendarEvents(memberId);
    }
}
