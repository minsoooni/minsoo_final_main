package com.spring.app.jobseeker.service;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.FileManager;
import com.spring.app.jobseeker.domain.*;
import com.spring.app.jobseeker.model.ApplyDAO;
import com.spring.app.jobseeker.model.MypageDAO;
import com.spring.app.jobseeker.model.ResumeDAO;
import com.spring.app.member.domain.MemberDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplyService_imple implements ApplyService {

    private final ApplyDAO applyDAO;
    private final ResumeDAO resumeDAO;
    private final MypageDAO mypageDAO;
    private final FileManager fileManager;
    private final ObjectMapper objectMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;


    @Override
    public boolean hasAlreadyApplied(long jobId, String memberid) {
        int count = applyDAO.selectApplicationCount(jobId, memberid);
        return count > 0;
    }


    @Transactional
    @Override
    public long submitApplication(long jobId, long resumeId, String memberid, List<MultipartFile> files) throws Exception {

        // 1. 이력서 전체 조회
        ResumeDTO resume = resumeDAO.selectResumeOne(resumeId);
        if (resume == null || !memberid.equals(resume.getMemberid())) {
            throw new IllegalArgumentException("이력서를 찾을 수 없거나 본인 이력서가 아닙니다.");
        }

        // 2. 이력서 하위 항목 조회
        List<ResumeEducationDTO> eduList = resumeDAO.selectEducationList(resumeId);
        List<ResumeCareerDTO> careerList = resumeDAO.selectCareerList(resumeId);
        List<ResumeLanguageDTO> langList = resumeDAO.selectLanguageList(resumeId);
        List<ResumePortfolioDTO> portfolioList = resumeDAO.selectPortfolioList(resumeId);
        List<ResumeAwardDTO> awardList = resumeDAO.selectAwardList(resumeId);
        List<ResumeTechstackDTO> techList = resumeDAO.selectTechstackList(resumeId);
        List<ResumeCertificateDTO> certList = resumeDAO.selectCertificateList(resumeId);

        // 3. 최종 학력 코드 결정
        String eduLevelCode = "EDU_NONE";
        if (eduList != null && !eduList.isEmpty()) {
            eduLevelCode = eduList.get(eduList.size() - 1).getEducationLevelCode();
        }

        // 4. 총 경력 개월수 계산
        int totalCareerMonths = 0;
        if (careerList != null) {
            for (ResumeCareerDTO c : careerList) {
                totalCareerMonths += calcMonths(c.getJoindate(), c.getLeavedate());
            }
        }

        // 5. 지원서 스냅샷 등록 (JSON 방식)
        SubmittedResumeDTO submitted = new SubmittedResumeDTO();
        submitted.setMemberid(memberid);
        submitted.setTitle(resume.getTitle());
        submitted.setAddress(resume.getAddress() != null ? resume.getAddress() : "");
        submitted.setSelfIntro(resume.getSelfIntro());
        submitted.setPhotoPath(resume.getPhotoPath());
        submitted.setEducation(toJson(eduList));
        submitted.setCareer(toJson(careerList));
        submitted.setLanguage(toJson(langList));
        submitted.setPortfolio(toJson(portfolioList));
        submitted.setAward(toJson(awardList));
        submitted.setEduLevelCode(eduLevelCode);
        submitted.setTotalCareerMonths(totalCareerMonths);
        submitted.setDesiredSalary(resume.getDesiredSalary());

        applyDAO.insertSubmittedResume(submitted);
        long submittedResumeId = submitted.getSubmittedResumeId();

        // 6. 기술스택 복사 (proficiency 포함)
        if (techList != null) {
            for (ResumeTechstackDTO tech : techList) {
                applyDAO.insertApplicationTechstack(submittedResumeId, tech.getSkillId(), tech.getProficiency());
            }
        }

        // 7. 자격증 복사 (acquired_date 포함)
        if (certList != null) {
            for (ResumeCertificateDTO cert : certList) {
                if (cert.getCertificateCode() != null && !cert.getCertificateCode().isEmpty()) {
                    applyDAO.insertApplicationCertificate(submittedResumeId, cert.getCertificateCode(), cert.getAcquiredDate());
                }
            }
        }

        // 8. 회원 정보 조회
        MemberDTO member = mypageDAO.selectMemberById(memberid);

        // 9. 지원 차수 결정
        int maxRound = applyDAO.selectMaxApplicationRound(jobId, memberid);
        int applicationRound = maxRound + 1;

        // 10. 입사지원 등록
        JobApplicationDTO application = new JobApplicationDTO();
        application.setJobId(jobId);
        application.setMemberid(memberid);
        application.setSubmittedResumeId(submittedResumeId);
        application.setName(member.getName());
        application.setBirthDate(member.getBirthDate() != null ? member.getBirthDate().toString() : "");
        application.setGender(member.getGender() != null ? member.getGender() : 0);
        application.setPhone(member.getPhone());
        application.setEmail(member.getEmail());
        application.setApplicationRound(applicationRound);

        applyDAO.insertJobApplication(application);
        long applicationId = application.getApplicationId();

        // 11. 첨부파일 등록
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String newFileName = fileManager.doFileUpload(file.getBytes(), file.getOriginalFilename(), uploadDir);
                    if (newFileName != null) {
                        ImageFileDTO imageFile = new ImageFileDTO();
                        imageFile.setTargetId(applicationId);
                        imageFile.setTargetType("APPLICATION");
                        imageFile.setFilecategory("ATTACH");
                        imageFile.setFileUrl(newFileName);
                        imageFile.setOriginalFilename(file.getOriginalFilename());
                        applyDAO.insertImageFile(imageFile);
                    }
                }
            }
        }

        return applicationId;
    }


    @Override
    public List<JobApplicationDTO> getApplicationList(String memberid) {
        return applyDAO.selectApplicationList(memberid);
    }

    @Override
    public JobApplicationDTO getApplicationDetail(long applicationId, String memberid) {
        return applyDAO.selectApplicationDetail(applicationId, memberid);
    }

    @Override
    public List<ImageFileDTO> getApplicationFiles(long applicationId) {
        return applyDAO.selectImageFileList(applicationId, "APPLICATION");
    }

    @Transactional
    @Override
    public int cancelApplication(long applicationId, String memberid) {
        return applyDAO.cancelApplication(applicationId, memberid);
    }

    @Override
    public Map<String, Integer> getApplicationStatusCounts(String memberid) {
        List<Map<String, Object>> list = applyDAO.selectApplicationStatusCounts(memberid);
        Map<String, Integer> result = new HashMap<>();
        result.put("total", 0);
        result.put("submitted", 0);
        result.put("reviewing", 0);
        result.put("interview", 0);
        result.put("passed", 0);
        result.put("rejected", 0);

        int total = 0;
        for (Map<String, Object> row : list) {
            int status = ((Number) row.get("PROCESS_STATUS")).intValue();
            int cnt = ((Number) row.get("CNT")).intValue();
            total += cnt;
            switch (status) {
                case 0: result.put("submitted", cnt); break;
                case 1: result.put("reviewing", cnt); break;
                case 2: result.merge("rejected", cnt, Integer::sum); break;
                case 3: result.put("interview", cnt); break;
                case 4: result.put("passed", cnt); break;
                case 5: result.merge("rejected", cnt, Integer::sum); break;
            }
        }
        result.put("total", total);
        return result;
    }

    @Override
    public SubmittedResumeDTO getSubmittedResume(long submittedResumeId) {
        return applyDAO.selectSubmittedResume(submittedResumeId);
    }

    @Override
    public List<Map<String, Object>> getApplicationTechstackList(long submittedResumeId) {
        return applyDAO.selectApplicationTechstackList(submittedResumeId);
    }

    @Override
    public List<Map<String, Object>> getApplicationCertificateList(long submittedResumeId) {
        return applyDAO.selectApplicationCertificateList(submittedResumeId);
    }


    // ===== private 유틸 ===== //

    private String toJson(List<?> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int calcMonths(String joindate, String leavedate) {
        try {
            if (joindate == null || joindate.isEmpty()) return 0;
            String jd = joindate.replace(".", "-").substring(0, 7);
            String ld;
            if (leavedate == null || leavedate.isEmpty()) {
                java.time.LocalDate now = java.time.LocalDate.now();
                ld = now.getYear() + "-" + String.format("%02d", now.getMonthValue());
            } else {
                ld = leavedate.replace(".", "-").substring(0, 7);
            }
            int jYear = Integer.parseInt(jd.substring(0, 4));
            int jMonth = Integer.parseInt(jd.substring(5, 7));
            int lYear = Integer.parseInt(ld.substring(0, 4));
            int lMonth = Integer.parseInt(ld.substring(5, 7));
            return (lYear - jYear) * 12 + (lMonth - jMonth);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> getApplicationHistory(long applicationId) {
        return applyDAO.selectApplicationHistory(applicationId);
    }
}
