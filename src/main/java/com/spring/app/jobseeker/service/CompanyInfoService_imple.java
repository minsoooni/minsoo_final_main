package com.spring.app.jobseeker.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.jobseeker.domain.CompanyInfoDTO;
import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.model.CompanyInfoDAO;

@Service
public class CompanyInfoService_imple implements CompanyInfoService {

    private final CompanyInfoDAO companyInfoDAO;

    public CompanyInfoService_imple(CompanyInfoDAO companyInfoDAO) {
        this.companyInfoDAO = companyInfoDAO;
    }

    // 기업 목록 조회
    @Override
    public List<CompanyInfoDTO> getCompanyList(Map<String, Object> paraMap) {
        return companyInfoDAO.selectCompanyList(paraMap);
    }

    // 기업 목록 총 건수
    @Override
    public int getCompanyListTotalCount(Map<String, Object> paraMap) {
        return companyInfoDAO.selectCompanyListTotalCount(paraMap);
    }

    // 기업 상세 조회
    @Override
    public CompanyInfoDTO getCompanyDetail(String memberId, String loginMemberId) {
        return companyInfoDAO.selectCompanyDetail(memberId, loginMemberId);
    }

    // 해당 기업의 진행중 채용공고 목록
    @Override
    public List<JobPostingListDTO> getCompanyJobPostings(String memberId) {
        List<JobPostingListDTO> list = companyInfoDAO.selectCompanyJobPostings(memberId);

        // skillNames → skillList 변환
        if (list != null) {
            for (JobPostingListDTO dto : list) {
                if (dto.getSkillNames() != null && !dto.getSkillNames().isEmpty()) {
                    dto.setSkillList(Arrays.asList(dto.getSkillNames().split(",")));
                }
            }
        }

        return list;
    }

    // 해당 기업의 진행중 채용공고 목록 (페이징)
    @Override
    public List<JobPostingListDTO> getCompanyJobPostingsPaged(Map<String, Object> paraMap) {
        List<JobPostingListDTO> list = companyInfoDAO.selectCompanyJobPostingsPaged(paraMap);

        // skillNames → skillList 변환
        if (list != null) {
            for (JobPostingListDTO dto : list) {
                if (dto.getSkillNames() != null && !dto.getSkillNames().isEmpty()) {
                    dto.setSkillList(Arrays.asList(dto.getSkillNames().split(",")));
                }
            }
        }

        return list;
    }

    // 해당 기업의 진행중 채용공고 총 건수
    @Override
    public int getCompanyJobPostingsCount(String memberId) {
        return companyInfoDAO.selectCompanyJobPostingsCount(memberId);
    }

    // 유사 기업 목록
    @Override
    public List<CompanyInfoDTO> getSimilarCompanies(String memberId, String industryCode) {
        return companyInfoDAO.selectSimilarCompanies(memberId, industryCode);
    }

    // 팔로우 토글
    @Transactional
    @Override
    public Map<String, Object> toggleFollow(String companyMemberId, String memberId) {

        Map<String, Object> resultMap = new HashMap<>();

        int isFollowed = companyInfoDAO.selectFollowStatus(companyMemberId, memberId);

        if (isFollowed > 0) {
            // 이미 팔로우 중 → 언팔로우
            companyInfoDAO.deleteFollow(companyMemberId, memberId);
            resultMap.put("action", "unfollowed");
        } else {
            // 팔로우 등록
            companyInfoDAO.insertFollow(companyMemberId, memberId);
            resultMap.put("action", "followed");
        }

        // 변경 후 팔로워 수 조회
        int followerCount = companyInfoDAO.selectFollowerCount(companyMemberId);
        resultMap.put("followerCount", followerCount);

        return resultMap;
    }
}
