package com.spring.app.jobseeker.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.common.domain.EducationDTO;
import com.spring.app.jobseeker.domain.JobCategoryDTO;
import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.domain.RegionDTO;
import com.spring.app.jobseeker.domain.ResumeDTO;
import com.spring.app.jobseeker.model.JobPostingDAO;
import com.spring.app.jobseeker.model.MypageDAO;

@Service
public class JobPostingService_imple implements JobPostingService {

    private final JobPostingDAO jobPostingDAO;
    private final MypageDAO mypageDAO;

    public JobPostingService_imple(JobPostingDAO jobPostingDAO, MypageDAO mypageDAO) {
        this.jobPostingDAO = jobPostingDAO;
        this.mypageDAO = mypageDAO;
    }

    // 채용공고 전체 건수 조회
    @Override
    public int getJobPostingCount(Map<String, Object> paraMap) {
        return jobPostingDAO.selectJobPostingCount(paraMap);
    }

    // 채용공고 목록 조회
    @Override
    public List<JobPostingListDTO> getJobPostingList(Map<String, Object> paraMap) {
        List<JobPostingListDTO> list = jobPostingDAO.selectJobPostingList(paraMap);
        for (JobPostingListDTO dto : list) {
            convertSkillNames(dto);
        }
        return list;
    }

    // 추천 채용공고 조회 (대표이력서 기반)
    @Override
    public List<JobPostingListDTO> getRecommendedJobPostings(String memberId) {
        Map<String, Object> paraMap = new HashMap<>();

        if (memberId != null) {
            ResumeDTO resume = mypageDAO.selectPrimaryResume(memberId);
            if (resume != null) {
                if (resume.getRegionCode() != null) {
                    paraMap.put("regionCode", resume.getRegionCode());
                }
                if (resume.getCategoryId() != null) {
                    paraMap.put("categoryId", resume.getCategoryId());
                }
                if (resume.getDesiredSalary() != null) {
                    paraMap.put("desiredSalary", resume.getDesiredSalary());
                }
                // 기술스택 매칭용 이력서 ID
                paraMap.put("resumeId", resume.getResumeId());
            }
        }

        List<JobPostingListDTO> list = jobPostingDAO.selectRecommendedJobPostings(paraMap);
        for (JobPostingListDTO dto : list) {
            convertSkillNames(dto);
        }
        return list;
    }

    // 인기 채용공고 조회 (조회수 높은순 3건)
    @Override
    public List<JobPostingListDTO> getPopularJobPostings() {
        List<JobPostingListDTO> list = jobPostingDAO.selectPopularJobPostings();
        for (JobPostingListDTO dto : list) {
            convertSkillNames(dto);
        }
        return list;
    }

    // 대표이력서 존재 여부 확인
    @Override
    public boolean hasPrimaryResume(String memberId) {
        if (memberId == null) return false;
        return mypageDAO.selectPrimaryResume(memberId) != null;
    }

    // 채용공고 상세 조회 (조회수 증가 포함)
    @Transactional
    @Override
    public JobPostingListDTO getJobPostingDetail(Long jobId) {
        jobPostingDAO.updateViewCount(jobId);
        JobPostingListDTO dto = jobPostingDAO.selectJobPostingDetail(jobId);
        if (dto != null) {
            convertSkillNames(dto);
        }
        return dto;
    }

    // === 마스터 데이터 조회 === //

    @Override
    public List<RegionDTO> getRegionList() {
        return jobPostingDAO.selectRegionList();
    }

    @Override
    public List<EducationDTO> getEduLevelList() {
        return jobPostingDAO.selectEduLevelList();
    }

    @Override
    public List<JobCategoryDTO> getJobCategoryList() {
        return jobPostingDAO.selectJobCategoryList();
    }

    @Override
    public List<Map<String, Object>> getSkillListGroupByCategory() {
        return jobPostingDAO.selectSkillListGroupByCategory();
    }

    // skillNames 콤마 문자열 -> List 변환 헬퍼
    private void convertSkillNames(JobPostingListDTO dto) {
        if (dto.getSkillNames() != null && !dto.getSkillNames().isEmpty()) {
            dto.setSkillList(Arrays.asList(dto.getSkillNames().split(",")));
        }
    }

    // === 최근본 공고 === //

    @Override
    public void saveViewLog(String memberId, Long jobId) {
        jobPostingDAO.mergeViewLog(memberId, jobId);
    }

    @Override
    public List<JobPostingListDTO> getRecentViewedJobs(String memberId) {
        List<JobPostingListDTO> list = jobPostingDAO.selectRecentViewedJobs(memberId);
        for (JobPostingListDTO dto : list) {
            convertSkillNames(dto);
        }
        return list;
    }

    @Override
    public List<JobPostingListDTO> getJobPostingsByIds(List<Long> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<JobPostingListDTO> list = jobPostingDAO.selectJobPostingsByIds(jobIds);
        for (JobPostingListDTO dto : list) {
            convertSkillNames(dto);
        }
        return list;
    }

    // 지원 여부 확인
    @Override
    public boolean hasAlreadyApplied(Long jobId, String memberId) {
        if (memberId == null) return false;
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("jobId", jobId);
        paraMap.put("memberId", memberId);
        return jobPostingDAO.checkAlreadyApplied(paraMap) > 0;
    }

    // 신고사유 목록
    @Override
    public List<Map<String, Object>> getReportReasonList() {
        return jobPostingDAO.selectReportReasonList();
    }

    // 신고 등록
    @Override
    public int submitReport(Long jobId, String memberId, Long reasonId, String reportContent) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("jobId", jobId);
        paraMap.put("memberId", memberId);
        paraMap.put("reasonId", reasonId);
        paraMap.put("reportContent", reportContent);
        return jobPostingDAO.insertReport(paraMap);
    }

    // 신고 중복 확인
    @Override
    public boolean hasAlreadyReported(Long jobId, String memberId) {
        if (memberId == null) return false;
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("jobId", jobId);
        paraMap.put("memberId", memberId);
        return jobPostingDAO.checkAlreadyReported(paraMap) > 0;
    }

    @Override
    public int getCompanyMemberStatus(String companyMemberId) {
        return jobPostingDAO.checkCompanyMemberStatus(companyMemberId);
    }

    @Override
    public boolean isFollowed(String companyMemberId, String memberId) {
        if (memberId == null) return false;
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("companyMemberId", companyMemberId);
        paraMap.put("memberId", memberId);
        return jobPostingDAO.checkFollowStatus(paraMap) > 0;
    }

    @Override
    public void toggleFollow(String companyMemberId, String memberId, boolean follow) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("companyMemberId", companyMemberId);
        paraMap.put("memberId", memberId);
        if (follow) {
            jobPostingDAO.insertFollow(paraMap);
        } else {
            jobPostingDAO.deleteFollow(paraMap);
        }
    }

    @Override
    public boolean isScraped(Long jobId, String memberId) {
        if (memberId == null) return false;
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("jobId", jobId);
        paraMap.put("memberId", memberId);
        return jobPostingDAO.checkScrapStatus(paraMap) > 0;
    }

    @Override
    public void toggleScrap(Long jobId, String memberId, boolean scrap) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("jobId", jobId);
        paraMap.put("memberId", memberId);
        if (scrap) {
            jobPostingDAO.insertScrap(paraMap);
        } else {
            jobPostingDAO.deleteScrap(paraMap);
        }
    }

    @Override
    public Map<String, Object> getApplicantStats(Long jobId) {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Object> basic = jobPostingDAO.selectApplicantStats(jobId);
        if (basic != null) {
            stats.putAll(basic);
        }
        List<Map<String, Object>> techTop5 = jobPostingDAO.selectApplicantTechTop5(jobId);
        List<Map<String, Object>> certTop5 = jobPostingDAO.selectApplicantCertTop5(jobId);

        // percent 계산
        int totalCount = basic != null ? ((Number) basic.getOrDefault("totalCount", 0)).intValue() : 0;
        for (Map<String, Object> t : techTop5) {
            int cnt = ((Number) t.get("count")).intValue();
            t.put("percent", totalCount > 0 ? Math.round((double) cnt / totalCount * 100) : 0);
        }
        for (Map<String, Object> ct : certTop5) {
            int cnt = ((Number) ct.get("count")).intValue();
            ct.put("percent", totalCount > 0 ? Math.round((double) cnt / totalCount * 100) : 0);
        }

        stats.put("techTop5", techTop5);
        stats.put("certTop5", certTop5);

        // 확장 통계 - 막대 그래프용 데이터
        int maxBarPx = 100;

        // 연령대
        Map<String, Object> ageRaw = jobPostingDAO.selectApplicantAgeStats(jobId);
        if (ageRaw != null) {
            int[] ageVals = {
                ((Number) ageRaw.getOrDefault("age20", 0)).intValue(),
                ((Number) ageRaw.getOrDefault("age30", 0)).intValue(),
                ((Number) ageRaw.getOrDefault("age40", 0)).intValue(),
                ((Number) ageRaw.getOrDefault("age50", 0)).intValue(),
                ((Number) ageRaw.getOrDefault("age60", 0)).intValue()
            };
            stats.put("ageData", toBarData(ageVals, maxBarPx));
        } else {
            stats.put("ageData", new java.util.ArrayList<>());
        }

        // 희망연봉
        Map<String, Object> salaryRaw = jobPostingDAO.selectApplicantSalaryStats(jobId);
        if (salaryRaw != null) {
            int[] salaryVals = {
                ((Number) salaryRaw.getOrDefault("s1", 0)).intValue(),
                ((Number) salaryRaw.getOrDefault("s2", 0)).intValue(),
                ((Number) salaryRaw.getOrDefault("s3", 0)).intValue(),
                ((Number) salaryRaw.getOrDefault("s4", 0)).intValue(),
                ((Number) salaryRaw.getOrDefault("s5", 0)).intValue()
            };
            stats.put("salaryData", toBarData(salaryVals, maxBarPx));
        } else {
            stats.put("salaryData", new java.util.ArrayList<>());
        }

        // 자격증 개수
        Map<String, Object> certRaw = jobPostingDAO.selectApplicantCertCountStats(jobId);
        if (certRaw != null) {
            int[] certVals = {
                ((Number) certRaw.getOrDefault("c0", 0)).intValue(),
                ((Number) certRaw.getOrDefault("c1", 0)).intValue(),
                ((Number) certRaw.getOrDefault("c2", 0)).intValue(),
                ((Number) certRaw.getOrDefault("c3", 0)).intValue(),
                ((Number) certRaw.getOrDefault("c4", 0)).intValue()
            };
            stats.put("certCountData", toBarData(certVals, maxBarPx));
        } else {
            stats.put("certCountData", new java.util.ArrayList<>());
        }

        // 학력별
        Map<String, Object> eduRaw = jobPostingDAO.selectApplicantEduStats(jobId);
        if (eduRaw != null) {
            int[] eduVals = {
                ((Number) eduRaw.getOrDefault("e0", 0)).intValue(),
                ((Number) eduRaw.getOrDefault("e1", 0)).intValue(),
                ((Number) eduRaw.getOrDefault("e2", 0)).intValue(),
                ((Number) eduRaw.getOrDefault("e3", 0)).intValue(),
                ((Number) eduRaw.getOrDefault("e4", 0)).intValue()
            };
            stats.put("eduData", toBarData(eduVals, maxBarPx));
        } else {
            stats.put("eduData", new java.util.ArrayList<>());
        }

        // 경력별
        Map<String, Object> careerRaw = jobPostingDAO.selectApplicantCareerStats(jobId);
        if (careerRaw != null) {
            int[] careerVals = {
                ((Number) careerRaw.getOrDefault("cr1", 0)).intValue(),
                ((Number) careerRaw.getOrDefault("cr2", 0)).intValue(),
                ((Number) careerRaw.getOrDefault("cr3", 0)).intValue(),
                ((Number) careerRaw.getOrDefault("cr4", 0)).intValue(),
                ((Number) careerRaw.getOrDefault("cr5", 0)).intValue()
            };
            stats.put("careerData", toBarData(careerVals, maxBarPx));
        } else {
            stats.put("careerData", new java.util.ArrayList<>());
        }

        return stats;
    }

    // 막대 그래프용 데이터 변환 (count → px)
    private List<Map<String, Object>> toBarData(int[] values, int maxPx) {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        int max = 0;
        for (int v : values) { if (v > max) max = v; }
        for (int v : values) {
            Map<String, Object> item = new HashMap<>();
            item.put("count", v);
            item.put("px", max > 0 ? Math.max((int) Math.round((double) v / max * maxPx), (v > 0 ? 5 : 0)) : 0);
            list.add(item);
        }
        return list;
    }


    // ================================================================
    //  매칭도 관련
    // ================================================================

    // 채용상세 매칭도 조회 (특정 공고 vs 내 대표이력서)
    @Override
    public Map<String, Object> getMatchScoreForJob(Long jobId, String memberId) {
        if (memberId == null) return null;

        ResumeDTO resume = mypageDAO.selectPrimaryResume(memberId);
        if (resume == null) return null;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("jobId", jobId);
        paraMap.put("regionCode", resume.getRegionCode());
        paraMap.put("categoryId", resume.getCategoryId());
        paraMap.put("desiredSalary", resume.getDesiredSalary() != null ? resume.getDesiredSalary() : 0);
        paraMap.put("resumeId", resume.getResumeId());

        Map<String, Object> result = jobPostingDAO.selectMatchScoreForJob(paraMap);
        if (result == null) return null;

        // 총점 계산 (만점 대비 퍼센트)
        int regionScore = ((Number) result.getOrDefault("regionScore", 0)).intValue();
        int categoryScore = ((Number) result.getOrDefault("categoryScore", 0)).intValue();
        int salaryScore = ((Number) result.getOrDefault("salaryScore", 0)).intValue();
        int techScore = ((Number) result.getOrDefault("techScore", 0)).intValue();
        int techTotalCount = ((Number) result.getOrDefault("techTotalCount", 0)).intValue();

        // 만점 = 5(지역) + 3(직무) + 2(연봉) + techTotal*2(기술)
        int maxScore = 5 + 3 + 2 + (techTotalCount > 0 ? techTotalCount * 2 : 2);
        int totalRaw = regionScore + categoryScore + salaryScore + techScore;
        int matchPercent = maxScore > 0 ? Math.min((int) Math.round((double) totalRaw / maxScore * 100), 100) : 0;

        result.put("matchScore", matchPercent);

        // 이력서의 희망지역/직무명도 같이 전달 (프론트 표시용)
        result.put("resumeRegionName", resume.getRegionName());
        result.put("resumeCategoryName", resume.getCategoryName());

        return result;
    }

    // 유사 공고 추천 (같은 직무/기술스택/지역 기준, 최대 4건)
    @Override
    public List<JobPostingListDTO> getSimilarJobPostings(Long jobId) {
        // 현재 공고의 직무/지역 정보 조회
        JobPostingListDTO currentPost = jobPostingDAO.selectJobPostingDetail(jobId);
        if (currentPost == null) return new java.util.ArrayList<>();

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("jobId", jobId);
        paraMap.put("categoryId", currentPost.getCategoryId());
        paraMap.put("regionCode", currentPost.getRegionCode());

        List<JobPostingListDTO> list = jobPostingDAO.selectSimilarJobPostings(paraMap);
        for (JobPostingListDTO dto : list) {
            convertSkillNames(dto);
            // 유사도 퍼센트 환산 (만점 = 3(직무) + 3(지역) + 기술수*2)
            if (dto.getMatchScore() != null) {
                int techTotalCount = dto.getSkillList() != null ? dto.getSkillList().size() : 1;
                int maxScore = 3 + 3 + (techTotalCount > 0 ? techTotalCount * 2 : 2);
                int pct = maxScore > 0 ? Math.min((int) Math.round((double) dto.getMatchScore() / maxScore * 100), 100) : 0;
                dto.setMatchScore(pct);
            }
        }
        return list;
    }
}
