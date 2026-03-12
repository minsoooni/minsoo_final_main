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

    @Override
    public MemberDTO getMemberInfo(String memberId) {
        return mypageDAO.selectMemberById(memberId);
    }

    @Override
    public ResumeDTO getPrimaryResume(String memberId) {
        return mypageDAO.selectPrimaryResume(memberId);
    }

    @Transactional
    @Override
    public int updateProfile(MemberDTO dto) {
        return mypageDAO.updateProfile(dto);
    }

    @Override
    public String getPassword(String memberId) {
        return mypageDAO.selectPassword(memberId);
    }

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

    @Override
    public Map<String, Integer> getDashboardStats(String memberId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalApplications", mypageDAO.selectTotalApplications(memberId));
        stats.put("activeApplications", mypageDAO.selectActiveApplications(memberId));
        stats.put("totalOffers", mypageDAO.selectTotalOffers(memberId));
        stats.put("unreadOffers", mypageDAO.selectUnreadOffers(memberId));
        stats.put("scrappedPosts", mypageDAO.selectScrappedCount(memberId));
        stats.put("followedCompanies", mypageDAO.selectFollowedCompanies(memberId));
        return stats;
    }

    @Override
    public List<Map<String, Object>> getRecentApplications(String memberId) {
        return mypageDAO.selectRecentApplications(memberId);
    }

    @Override
    public List<Map<String, Object>> getRecentViewedPosts(String memberId) {
        return mypageDAO.selectRecentViewedPosts(memberId);
    }
}
