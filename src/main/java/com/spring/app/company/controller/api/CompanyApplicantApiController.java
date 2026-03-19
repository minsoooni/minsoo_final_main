package com.spring.app.company.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.company.domain.ApplicantDetailDTO;
import com.spring.app.company.domain.ApplicantListDTO;
import com.spring.app.company.domain.ImageFileDTO;
import com.spring.app.company.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Company - applicant API", description = "기업 지원자 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company/applicant/api")
public class CompanyApplicantApiController {

    private final CompanyService service;
    
    private String mapProcessStatus(Integer processStatus) {
        if (processStatus == null) return "UNREAD";

        switch (processStatus) {
            case 0: return "UNREAD";
            case 1: return "READ";
            case 2: return "REJECTED";   // 서류탈락
            case 3: return "INTERVIEW";  // 면접요청
            case 4: return "PASSED";     // 합격
            case 5: return "REJECTED";   // 불합격
            default: return "UNREAD";
        }
    }
    
    
    // 지원가 목록 조회
    @Operation(summary = "지원자 목록 조회", description = "기업 회원이 등록한 공고에 지원한 지원자 목록을 조회한다.")
    @GetMapping("/list")
    public List<ApplicantListDTO> applicantList(Authentication authentication,
                                                @RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "processStatus", required = false) Integer processStatus,
                                                @RequestParam(value = "jobId", required = false) Long jobId) {

        String memberId = authentication.getName();

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("keyword", keyword);
        paraMap.put("processStatus", processStatus);
        paraMap.put("jobId", jobId);

        return service.selectApplicantList(paraMap);
    }

    
    
    // 지원자 상세 조회
    @Operation(summary = "지원자 이력서 조회", description = "기업 회원이 등록한 공고에 지원한 이력서를 조회한다.")
    @GetMapping("/detail")
    public ModelAndView applicantDetail(@RequestParam("applicationId") Long applicationId,
                                        Authentication authentication,
                                        ModelAndView mav) {

        if (authentication == null) {
            mav.setViewName("redirect:/member/login");
            return mav;
        }

        String companyMemberId = authentication.getName();

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("applicationId", applicationId);
        paraMap.put("memberId", companyMemberId);

        // 상세 진입 시 최초 열람 처리
        service.readApplicantDetail(paraMap);

        // 기업용 지원자 상세 조회
        ApplicantDetailDTO dto = service.getApplicantDetailForCompany(paraMap);

        if (dto == null) {
            mav.setViewName("redirect:/company/applicant/list");
            return mav;
        }

        List<ImageFileDTO> fileList = service.getApplicationFiles(applicationId);

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
        appDetail.put("files", fileList);

        // tbl_job_application 스냅샷
        appDetail.put("name", dto.getName());
        appDetail.put("birthDate", dto.getBirthDate());
        appDetail.put("gender", dto.getGender());
        appDetail.put("phone", dto.getPhone());
        appDetail.put("email", dto.getEmail());

        // tbl_submitted_resume 스냅샷
        appDetail.put("selfIntro", dto.getSelfIntro());
        appDetail.put("education", dto.getEducation());
        appDetail.put("career", dto.getCareer());
        appDetail.put("language", dto.getLanguage());
        appDetail.put("portfolio", dto.getPortfolio());
        appDetail.put("award", dto.getAward());
        appDetail.put("address", dto.getAddress());
        appDetail.put("photoPath", dto.getPhotoPath());
        appDetail.put("desiredSalary", dto.getDesiredSalary());

        List<Map<String, Object>> techstackList = service.getApplicationTechstackList(dto.getSubmittedResumeId());
        List<Map<String, Object>> certificateList = service.getApplicationCertificateList(dto.getSubmittedResumeId());

        appDetail.put("techstacks", techstackList);
        appDetail.put("certificates", certificateList);

        mav.addObject("appDetail", appDetail);
        mav.addObject("activeMenu", "applicant");
        mav.setViewName("company/applicant/applicant_detail");

        return mav;
    }

    
    // 지원자 상태 변경
    @Operation(summary = "지원자 상태 변경", description = "기업 회원이 등록한 공고에 지원한 지원자 상태를 변경한다.")
    @PutMapping("/status")
    public Map<String, Object> updateApplicantStatus(@RequestBody Map<String, Object> request,
                                                     Authentication authentication) {

        String memberId = authentication.getName();

        Long applicationId = Long.valueOf(String.valueOf(request.get("applicationId")));
        Integer prevStatus = Integer.valueOf(String.valueOf(request.get("prevStatus")));
        Integer newStatus = Integer.valueOf(String.valueOf(request.get("newStatus")));

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("applicationId", applicationId);
        paraMap.put("prevStatus", prevStatus);
        paraMap.put("newStatus", newStatus);
        paraMap.put("memberId", memberId);

        boolean success = service.updateApplicantStatus(paraMap);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "상태가 변경되었습니다." : "상세 확인 후 상태를 변경할 수 있습니다.");

        return result;
    }
    
    
}