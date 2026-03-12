package com.spring.app.jobseeker.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.domain.CompanyInfoDTO;
import com.spring.app.jobseeker.model.CompanyInfoDAO;

@Service
public class FollowCompanyService_imple implements FollowCompanyService {

    private final CompanyInfoDAO companyInfoDAO;

    public FollowCompanyService_imple(CompanyInfoDAO companyInfoDAO) {
        this.companyInfoDAO = companyInfoDAO;
    }

    // 팔로우한 기업 목록 조회
    @Override
    public List<CompanyInfoDTO> getFollowedCompanyList(Map<String, Object> paraMap) {
        return companyInfoDAO.selectFollowedCompanyList(paraMap);
    }

    // 팔로우한 기업 총 건수
    @Override
    public int getFollowedCompanyCount(Map<String, Object> paraMap) {
        return companyInfoDAO.selectFollowedCompanyCount(paraMap);
    }

    // 팔로우 해제
    @Transactional
    @Override
    public int removeFollow(String memberId, String companyMemberId) {
        return companyInfoDAO.deleteFollow(companyMemberId, memberId);
    }
}
