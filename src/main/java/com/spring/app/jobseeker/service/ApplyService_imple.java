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
import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.model.NotificationDAO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ApplyService_imple implements ApplyService {

    // === 중복 상수화 === //
    private static final String TARGET_TYPE_APPLICATION = "APPLICATION";
    private static final String KEY_REJECTED = "rejected";

    private final ApplyDAO applyDAO;
    private final ResumeDAO resumeDAO;
    private final MypageDAO mypageDAO;
    private final FileManager fileManager;
    private final ObjectMapper objectMapper;  // Java 객체 ↔ JSON 문자열 변환 도구
    private final NotificationDAO notificationDAO;

    @Value("${file.upload-dir}")
    private String uploadDir;


    // 중복 지원 여부 확인
    @Override
    public boolean hasAlreadyApplied(long jobId, String memberid) {
        int count = applyDAO.selectApplicationCount(jobId, memberid);
        return count > 0;
    }


    
    // 입사지원 제출
    // 이력서 조회 → JSON 스냅샷 생성 → 기술/자격증 복사 → 지원 등록 → 첨부파일 저장
    @Transactional
    @Override
    public long submitApplication(long jobId, long resumeId, String memberid, List<MultipartFile> files) throws Exception {

        // 이력서 원본 조회 + 본인 확인
        ResumeDTO resume = resumeDAO.selectResumeOne(resumeId);
        if (resume == null || !memberid.equals(resume.getMemberid())) {
            throw new IllegalArgumentException("이력서를 찾을 수 없거나 본인 이력서가 아닙니다.");
        }

        // 이력서 하위 항목 조회
        List<ResumeEducationDTO> eduList = resumeDAO.selectEducationList(resumeId);
        List<ResumeCareerDTO> careerList = resumeDAO.selectCareerList(resumeId);
        List<ResumeLanguageDTO> langList = resumeDAO.selectLanguageList(resumeId);
        List<ResumePortfolioDTO> portfolioList = resumeDAO.selectPortfolioList(resumeId);
        List<ResumeAwardDTO> awardList = resumeDAO.selectAwardList(resumeId);
        List<ResumeTechstackDTO> techList = resumeDAO.selectTechstackList(resumeId);
        List<ResumeCertificateDTO> certList = resumeDAO.selectCertificateList(resumeId);

        // 최종 학력 코드 (학력 목록의 마지막 항목)
        String eduLevelCode = "EDU_NONE";
        if (eduList != null && !eduList.isEmpty()) {
            eduLevelCode = eduList.get(eduList.size() - 1).getEducationLevelCode();
        }

        // 총 경력 개월수 합산
        int totalCareerMonths = 0;
        if (careerList != null) {
            for (ResumeCareerDTO c : careerList) {
                totalCareerMonths += calcMonths(c.getJoindate(), c.getLeavedate());
            }
        }

        // 이력서 스냅샷 저장 (학력/경력 등은 toJson()으로 JSON 변환 → CLOB 저장)
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

        // 기술스택 복사 (별도 테이블에 행 단위 저장)
        if (techList != null) {
            for (ResumeTechstackDTO tech : techList) {
                applyDAO.insertApplicationTechstack(submittedResumeId, tech.getSkillId(), tech.getProficiency());
            }
        }

        // 자격증 복사 (별도 테이블에 행 단위 저장)
        if (certList != null) {
            for (ResumeCertificateDTO cert : certList) {
                if (cert.getCertificateCode() != null && !cert.getCertificateCode().isEmpty()) {
                    applyDAO.insertApplicationCertificate(submittedResumeId, cert.getCertificateCode(), cert.getAcquiredDate());
                }
            }
        }

        // 회원 정보 조회 (지원 당시 이름/연락처 스냅샷용)
        MemberDTO member = mypageDAO.selectMemberById(memberid);

        // 지원 차수 결정 (동일 공고 재지원 시 1차 → 2차 → ...)
        int maxRound = applyDAO.selectMaxApplicationRound(jobId, memberid);
        int applicationRound = maxRound + 1;

        // 입사지원 등록
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

        // 첨부파일 저장
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String newFileName = fileManager.doFileUpload(file.getBytes(), file.getOriginalFilename(), uploadDir);
                    if (newFileName != null) {
                        ImageFileDTO imageFile = new ImageFileDTO();
                        imageFile.setTargetId(applicationId);
                        imageFile.setTargetType(TARGET_TYPE_APPLICATION);
                        imageFile.setFilecategory("ATTACH");
                        imageFile.setFileUrl(newFileName);
                        imageFile.setOriginalFilename(file.getOriginalFilename());
                        applyDAO.insertImageFile(imageFile);
                    }
                }
            }
        }

        // 기업에게 알림 발송
        try {
            Map<String, Object> jobInfo = applyDAO.selectJobPostingForNoti(jobId);
            if (jobInfo != null) {
                NotificationDTO noti = new NotificationDTO();
                noti.setFkMemberId((String) jobInfo.get("companyMemberId"));
                noti.setType(TARGET_TYPE_APPLICATION);
                noti.setTitle("새로운 지원자 안내");
                noti.setMessage("[" + jobInfo.get("postTitle") + "]에 " + member.getName() + "님이 지원했습니다");
                noti.setLinkUrl("/company/applicant/list?jobId=" + jobId);
                notificationDAO.insertNotification(noti);
            }
        } catch (Exception e) {
            // 알림 실패해도 지원 자체는 성공 처리
        }

        return applicationId;
    }


    // 내 지원 내역 목록 조회
    @Override
    public List<JobApplicationDTO> getApplicationList(String memberid) {
        return applyDAO.selectApplicationList(memberid);
    }

    // 지원서 상세 조회
    @Override
    public JobApplicationDTO getApplicationDetail(long applicationId, String memberid) {
        return applyDAO.selectApplicationDetail(applicationId, memberid);
    }

    // 지원서 첨부파일 목록 조회
    @Override
    public List<ImageFileDTO> getApplicationFiles(long applicationId) {
        return applyDAO.selectImageFileList(applicationId, TARGET_TYPE_APPLICATION);
    }

    // 입사지원 취소 (접수 상태일 때만 가능)
    @Transactional
    @Override
    public int cancelApplication(long applicationId, String memberid) {
        return applyDAO.cancelApplication(applicationId, memberid);
    }

    
    // 지원 진행상태별 건수 집계
    // process_status: 0=접수, 1=검토중, 2=서류탈락, 3=면접, 4=합격, 5=최종불합격
    @Override
    public Map<String, Integer> getApplicationStatusCounts(String memberid) {
        List<Map<String, Object>> list = applyDAO.selectApplicationStatusCounts(memberid);
        Map<String, Integer> result = new HashMap<>();
        result.put("total", 0);
        result.put("submitted", 0);
        result.put("reviewing", 0);
        result.put("interview", 0);
        result.put("passed", 0);
        result.put(KEY_REJECTED, 0);

        int total = 0;
        for (Map<String, Object> row : list) {
            int status = ((Number) row.get("PROCESS_STATUS")).intValue();
            int cnt = ((Number) row.get("CNT")).intValue();
            total += cnt;
            switch (status) {
                case 0: result.put("submitted", cnt); break;
                case 1: result.put("reviewing", cnt); break;
                case 2: result.merge(KEY_REJECTED, cnt, Integer::sum); break;  // 서류탈락 → rejected 합산
                case 3: result.put("interview", cnt); break;
                case 4: result.put("passed", cnt); break;
                case 5: result.merge(KEY_REJECTED, cnt, Integer::sum); break;  // 최종불합격 → rejected 합산
                default: break;  // 정의되지 않은 상태값은 무시
            }
        }
        result.put("total", total);
        return result;
    }

    // 제출 이력서 스냅샷 조회
    @Override
    public SubmittedResumeDTO getSubmittedResume(long submittedResumeId) {
        return applyDAO.selectSubmittedResume(submittedResumeId);
    }

    // 지원서 기술스택 목록 조회
    @Override
    public List<Map<String, Object>> getApplicationTechstackList(long submittedResumeId) {
        return applyDAO.selectApplicationTechstackList(submittedResumeId);
    }

    // 지원서 자격증 목록 조회
    @Override
    public List<Map<String, Object>> getApplicationCertificateList(long submittedResumeId) {
        return applyDAO.selectApplicationCertificateList(submittedResumeId);
    }


    // ===== private 유틸 ===== //

    // Java List → JSON 문자열 변환 (null이면 null 반환)
    private String toJson(List<?> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 입사일~퇴사일 개월수 계산 (재직중이면 현재까지)
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

    // 지원 진행상태 이력 조회
    @Override
    public List<Map<String, Object>> getApplicationHistory(long applicationId) {
        return applyDAO.selectApplicationHistory(applicationId);
    }
}
