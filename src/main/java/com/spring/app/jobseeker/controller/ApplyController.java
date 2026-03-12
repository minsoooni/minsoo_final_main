package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.jobseeker.domain.*;
import com.spring.app.jobseeker.service.ApplyService;
import com.spring.app.jobseeker.service.JobPostingService;
import com.spring.app.jobseeker.service.ResumeService;


@Controller
@RequestMapping("/jobseeker")
public class ApplyController {

    private final ApplyService applyService;
    private final ResumeService resumeService;
    private final JobPostingService jobPostingService;

    public ApplyController(ApplyService applyService,
                           ResumeService resumeService,
                           JobPostingService jobPostingService) {
        this.applyService = applyService;
        this.resumeService = resumeService;
        this.jobPostingService = jobPostingService;
    }



    // 1. 지원하기 폼
    @GetMapping("apply/form/{jobId}")
    public ModelAndView applyForm(@PathVariable("jobId") Long jobId,
                                  Principal principal,
                                  ModelAndView mav) {

        if (principal == null) {
            mav.setViewName("redirect:/member/login");
            return mav;
        }

        String memberid = principal.getName();

        JobPostingListDTO post = jobPostingService.getJobPostingDetail(jobId);
        if (post == null) {
            mav.setViewName("redirect:/job/list");
            return mav;
        }

        if (applyService.hasAlreadyApplied(jobId, memberid)) {
            mav.setViewName("redirect:/job/detail/" + jobId);
            return mav;
        }

        Map<String, String> postMap = new HashMap<>();
        postMap.put("id", String.valueOf(post.getJobId()));
        postMap.put("title", post.getTitle());
        postMap.put("companyName", post.getCompanyName());
        postMap.put("region", (post.getParentRegionName() != null ? post.getParentRegionName() + " " : "")
                + (post.getRegionName() != null ? post.getRegionName() : ""));
        postMap.put("career", post.getCareerType());

        if ("always".equals(post.getDeadlineType())) {
            postMap.put("deadline", "상시채용");
        } else {
            postMap.put("deadline", post.getDeadlineAt() != null ? "~ " + post.getDeadlineAt() : "");
        }
        mav.addObject("post", postMap);

        List<ResumeDTO> allResumes = resumeService.selectResumeListByMember(memberid);
        List<Map<String, Object>> resumes = new ArrayList<>();
        for (ResumeDTO r : allResumes) {
            if (r.getWriteStatus() == 1 && r.getIsDeleted() == 0) {
                Map<String, Object> rMap = new HashMap<>();
                rMap.put("id", r.getResumeId());
                rMap.put("title", r.getTitle());
                rMap.put("career", r.getCareerSummary() != null ? r.getCareerSummary() : "신입");
                rMap.put("role", r.getCategoryName() != null ? r.getCategoryName() : "미지정");
                rMap.put("updatedAt", r.getUploadedAt());
                rMap.put("isDefault", r.getIsPrimary() == 1);
                resumes.add(rMap);
            }
        }

        if (resumes.isEmpty()) {
            mav.setViewName("redirect:/jobseeker/resume/list");
            return mav;
        }

        mav.addObject("resumes", resumes);
        mav.setViewName("jobseeker/apply/form");
        return mav;
    }


   
    // 2. 지원 제출
    @PostMapping("apply/{jobId}")
    public String applySubmit(@PathVariable("jobId") Long jobId,
                              @RequestParam("resumeId") Long resumeId,
                              @RequestParam(value = "files", required = false) List<MultipartFile> files,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        String memberid = principal.getName();

        try {
            if (applyService.hasAlreadyApplied(jobId, memberid)) {
                redirectAttributes.addFlashAttribute("errorMsg", "이미 지원한 공고입니다.");
                return "redirect:/job/detail/" + jobId;
            }

            applyService.submitApplication(jobId, resumeId, memberid, files);
            return "redirect:/jobseeker/apply/done";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "지원 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/jobseeker/apply/form/" + jobId;
        }
    }


   
    // 3. 지원 완료
    @GetMapping("apply/done")
    public ModelAndView applyDone(ModelAndView mav) {
        mav.setViewName("jobseeker/apply/done");
        return mav;
    }


 
    // 4. 지원 내역 목록
    @GetMapping("application/list")
    public ModelAndView applicationList(Principal principal,
                                         @RequestParam(value = "status", required = false, defaultValue = "ALL") String statusFilter,
                                         @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                         @RequestParam(value = "sort", required = false, defaultValue = "latest") String sort) {

        ModelAndView mav = new ModelAndView();
        mav.addObject("activeMenu", "application");
        mav.addObject("status", statusFilter);

        if (principal == null) {
            mav.setViewName("redirect:/member/login");
            return mav;
        }

        String memberid = principal.getName();

        List<JobApplicationDTO> appList = applyService.getApplicationList(memberid);

        List<Map<String, Object>> applications = new ArrayList<>();
        for (JobApplicationDTO dto : appList) {

            // 키워드 필터링 (공고명, 회사명)
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.trim().toLowerCase();
                String postTitle = dto.getPostTitle() != null ? dto.getPostTitle().toLowerCase() : "";
                String companyName = dto.getCompanyName() != null ? dto.getCompanyName().toLowerCase() : "";
                if (!postTitle.contains(kw) && !companyName.contains(kw)) {
                    continue;
                }
            }

            Map<String, Object> app = new HashMap<>();
            app.put("id", dto.getApplicationId());
            app.put("postId", dto.getJobId());
            app.put("postTitle", dto.getPostTitle());
            app.put("companyName", dto.getCompanyName());
            app.put("companyLogo", dto.getCompanyLogo());
            app.put("appliedAt", dto.getAppliedAt());
            app.put("status", mapProcessStatus(dto.getProcessStatus()));
            app.put("statusText", dto.getProcessStatusText());
            app.put("region", dto.getRegionName());
            app.put("role", dto.getCategoryName());
            app.put("employmentType", dto.getWorkType());
            app.put("salary", dto.getSalary() != null ? String.format("%,d만원", dto.getSalary()) : null);
            app.put("deadline", dto.getDeadlineAt());
            app.put("resumeTitle", dto.getResumeTitle());
            app.put("canCancel", dto.getProcessStatus() == 0 && dto.getApplicationStatus() == 0);

            // 진행상태 이력 조회
            List<Map<String, Object>> history = applyService.getApplicationHistory(dto.getApplicationId());
            app.put("history", history);
            app.put("viewedAt", dto.getViewedAt());
            app.put("processStatus", dto.getProcessStatus());

            // 히스토리에서 각 단계별 날짜 추출
            Map<Integer, String> statusDates = new HashMap<>();
            for (Map<String, Object> h : history) {
                int newStatus = ((Number) h.get("newStatus")).intValue();
                String changedAt = (String) h.get("changedAt");
                statusDates.put(newStatus, changedAt);
            }
            app.put("viewedDate", statusDates.getOrDefault(1, dto.getViewedAt()));
            app.put("interviewDate", statusDates.get(3));
            app.put("resultDate", statusDates.containsKey(4) ? statusDates.get(4)
                                : statusDates.containsKey(5) ? statusDates.get(5)
                                : statusDates.get(2));

            // 면접 단계를 거쳤는지 판별
            boolean hasInterview = statusDates.containsKey(3);
            boolean hasViewed = dto.getProcessStatus() >= 1 || statusDates.containsKey(1);
            app.put("hasInterview", hasInterview);
            app.put("hasViewed", hasViewed);

            // 탭 필터링
            String mappedStatus = (String) app.get("status");
            if (!"ALL".equals(statusFilter)) {
                boolean match = false;
                if ("SUBMITTED".equals(statusFilter) && ("UNREAD".equals(mappedStatus))) match = true;
                if ("REVIEWING".equals(statusFilter) && ("READ".equals(mappedStatus))) match = true;
                if ("INTERVIEW".equals(statusFilter) && "INTERVIEW".equals(mappedStatus)) match = true;
                if ("PASSED".equals(statusFilter) && "PASSED".equals(mappedStatus)) match = true;
                if ("REJECTED".equals(statusFilter) && "REJECTED".equals(mappedStatus)) match = true;
                if (!match) continue;
            }

            applications.add(app);
        }

        // 정렬
        if ("oldest".equals(sort)) {
            applications.sort((a, b) -> {
                String dateA = (String) a.get("appliedAt");
                String dateB = (String) b.get("appliedAt");
                return (dateA != null ? dateA : "").compareTo(dateB != null ? dateB : "");
            });
        } else if ("company".equals(sort)) {
            applications.sort((a, b) -> {
                String compA = (String) a.get("companyName");
                String compB = (String) b.get("companyName");
                return (compA != null ? compA : "").compareTo(compB != null ? compB : "");
            });
        }
        // "latest"는 SQL에서 이미 applied_at DESC로 가져오므로 별도 정렬 불필요

        mav.addObject("applications", applications);

        Map<String, Integer> statusCounts = applyService.getApplicationStatusCounts(memberid);
        mav.addObject("totalCount", statusCounts.get("total"));
        mav.addObject("submittedCount", statusCounts.get("submitted"));
        mav.addObject("reviewingCount", statusCounts.get("reviewing"));
        mav.addObject("interviewCount", statusCounts.get("interview"));
        mav.addObject("passedCount", statusCounts.get("passed"));
        mav.addObject("rejectedCount", statusCounts.get("rejected"));

        mav.addObject("keyword", keyword);
        mav.addObject("sort", sort);

        mav.setViewName("jobseeker/application/list");
        return mav;
    }


   
    // 5. 지원서 상세 
    @GetMapping("application/detail/{id}")
    public ModelAndView applicationDetail(@PathVariable("id") Long id,
                                          Principal principal,
                                          ModelAndView mav) {

        if (principal == null) {
            mav.setViewName("redirect:/member/login");
            return mav;
        }

        String memberid = principal.getName();

        JobApplicationDTO dto = applyService.getApplicationDetail(id, memberid);
        if (dto == null) {
            mav.setViewName("redirect:/jobseeker/application/list");
            return mav;
        }

        List<ImageFileDTO> fileList = applyService.getApplicationFiles(id);

        Map<String, Object> appDetail = new HashMap<>();
        appDetail.put("id", dto.getApplicationId());
        appDetail.put("postId", dto.getJobId());
        appDetail.put("postTitle", dto.getPostTitle());
        appDetail.put("companyName", dto.getCompanyName());
        appDetail.put("region", dto.getRegionName());
        appDetail.put("role", dto.getCategoryName());
        appDetail.put("status", mapProcessStatus(dto.getProcessStatus()));
        appDetail.put("statusText", dto.getProcessStatusText());
        appDetail.put("appliedAt", dto.getAppliedAt());
        appDetail.put("viewedAt", dto.getViewedAt());
        appDetail.put("resumeTitle", dto.getResumeTitle());
        appDetail.put("canEdit", false);
        appDetail.put("canCancel", dto.getProcessStatus() == 0 && dto.getApplicationStatus() == 0);
        appDetail.put("files", fileList);

        // 회원 스냅샷 정보 (tbl_job_application)
        appDetail.put("name", dto.getName());
        appDetail.put("birthDate", dto.getBirthDate());
        appDetail.put("gender", dto.getGender());
        appDetail.put("phone", dto.getPhone());
        appDetail.put("email", dto.getEmail());

        // 이력서 스냅샷 정보 (tbl_submitted_resume)
        SubmittedResumeDTO snapshot = applyService.getSubmittedResume(dto.getSubmittedResumeId());
        if (snapshot != null) {
            appDetail.put("selfIntro", snapshot.getSelfIntro());
            appDetail.put("education", snapshot.getEducation());
            appDetail.put("career", snapshot.getCareer());
            appDetail.put("language", snapshot.getLanguage());
            appDetail.put("portfolio", snapshot.getPortfolio());
            appDetail.put("award", snapshot.getAward());
            appDetail.put("address", snapshot.getAddress());
            appDetail.put("photoPath", snapshot.getPhotoPath());
            appDetail.put("desiredSalary", snapshot.getDesiredSalary());
        }

        // 기술스택 / 자격증 (별도 스냅샷 테이블 - 상세 정보)
        List<Map<String, Object>> techstackList = applyService.getApplicationTechstackList(dto.getSubmittedResumeId());
        List<Map<String, Object>> certificateList = applyService.getApplicationCertificateList(dto.getSubmittedResumeId());
        appDetail.put("techstacks", techstackList);
        appDetail.put("certificates", certificateList);

        mav.addObject("appDetail", appDetail);
        mav.addObject("activeMenu", "application");

        mav.setViewName("jobseeker/application/detail");
        return mav;
    }


   
    // 6. 지원 취소 (form POST)
    @PostMapping("application/{id}/cancel")
    public String cancelApplication(@PathVariable("id") Long id,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        String memberid = principal.getName();
        int n = applyService.cancelApplication(id, memberid);

        if (n == 1) {
            redirectAttributes.addFlashAttribute("successMsg", "지원이 취소되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "지원 취소에 실패했습니다. 이미 열람되었거나 취소된 지원입니다.");
        }

        return "redirect:/jobseeker/application/list";
    }


  
    // process_status(숫자) → 상태 문자열
    private String mapProcessStatus(int processStatus) {
        switch (processStatus) {
            case 0: return "UNREAD";
            case 1: return "READ";
            case 2: return "REJECTED";
            case 3: return "INTERVIEW";
            case 4: return "PASSED";
            case 5: return "REJECTED";
            default: return "UNREAD";
        }
    }
}
