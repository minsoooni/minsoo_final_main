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
    
    
    // 지원자 목록 조회(페이징 처리)
    @Operation(summary = "지원자 목록 조회", description = "기업 회원이 등록한 공고에 지원한 지원자 목록을 페이징 처리하여 조회한다.")
    @GetMapping("/list")
    public Map<String, Object> applicantList(Authentication authentication,
                                             @RequestParam(value = "keyword", required = false) String keyword,
                                             @RequestParam(value = "processStatus", required = false) Integer processStatus,
                                             @RequestParam(value = "jobId", required = false) Long jobId,
                                             @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo) {

        String memberId = authentication.getName();

        int sizePerPage = 10;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("keyword", keyword);
        paraMap.put("processStatus", processStatus);
        paraMap.put("jobId", jobId);

        int totalCount = service.selectApplicantCount(paraMap);
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);

        if (pageNo < 1) {
            pageNo = 1;
        }
        if (totalPage > 0 && pageNo > totalPage) {
            pageNo = totalPage;
        }

        int startRow = ((pageNo - 1) * sizePerPage) + 1;
        int endRow = startRow + sizePerPage - 1;

        paraMap.put("startRow", startRow);
        paraMap.put("endRow", endRow);

        List<ApplicantListDTO> list = service.selectApplicantListPaging(paraMap);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("sizePerPage", sizePerPage);
        result.put("currentShowPageNo", pageNo);
        result.put("totalPage", totalPage);

        return result;
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
        //System.out.println(dto);
        /*
        ApplicantDetailDTO(applicationId=35, jobId=1057, memberId=solee7966, submittedResumeId=35, applicationRound=1, applicationStatus=0, 
        				processStatus=1, processStatusText=열람, appliedAt=Wed Mar 25 11:43:33 GMT+09:00 2026, cancelledAt=null, 
        				viewedAt=Wed Mar 25 11:50:47 GMT+09:00 2026, postTitle=백엔드 개발자 (Java/Spring) 채용, companyName=(주)네오테크 솔루션즈, 
        				regionName=강남구, categoryName=백엔드, workType=정규직, salary=4000, deadlineAt=Wed Apr 15 23:59:00 GMT+09:00 2026, 
        				name=안태훈, birthDate=Sat Nov 11 00:00:00 GMT+09:00 2000, gender=1, phone=010-4533-1386, email=solee7966@naver.com, 
        				resumeTitle=백엔드 개발자 이력서, selfIntro=안정적인 서비스를 만드는 백엔드 개발자를 목표로 성장하고 있습니다.
					        		Java와 Spring Boot를 기반으로 REST API를 설계하고, MySQL/Oracle DB와 연동하여 데이터를 처리하는 프로젝트를 수행해왔습니다.
					
					        		특히 단순한 기능 구현을 넘어, 서비스의 안정성과 확장성을 고려하는 개발을 지향합니다.
					        		프로젝트 진행 중에는 API 응답 속도 개선을 위해 쿼리 최적화와 인덱스 설계를 적용한 경험이 있으며, 이를 통해 실제 성능 개선을 이루었습니다.
					
					        		또한 Git을 활용한 협업 경험을 통해 코드 리뷰와 브랜치 전략의 중요성을 이해하고 있으며, 문제 발생 시 로그를 기반으로 원인을 분석하고 해결하는 능력을 키워왔습니다.
					
					        		최근에는 Docker와 CI/CD 환경에 관심을 가지고 학습하며, 운영 환경까지 고려하는 개발자로 성장하고 있습니다.
					        		앞으로도 단순히 동작하는 코드가 아닌, 유지보수와 확장성이 뛰어난 서비스를 만드는 개발자가 되고자 합니다., 
        		education=[{"educationId":145,"resumeId":34,"educationLevelCode":"EDU_COLLEGE_4","schoolname":"쌍용대학교","major":"소프트웨어학",
        					"enrolldate":"2019-03-04","graduationdate":"2025-02-28","status":"0","sort":0,"educationLevelName":"대학교(4년)"}], 
        					career=null, language=null, portfolio=[{"portfolioId":99,"resumeId":34,"portfolioType":0,"link":"https://www.naver.com/",
        					"filepath":null,"originalFilename":null,"portfolioTitle":null,"portfolioUrl":null,"portfolioDesc":null,"createdAt":"2026-03-25","sort":0}],
        					award=null, address=경기 하남시 수리북로 29, photoPath=/images/jobseeker/20260325024136_362e5d0abd67435698e87b5ba092de6d.png, desiredSalary=3000)
		*/
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
        //System.out.println("dto.getPhotoPath()"+dto.getPhotoPath());
        //dto.getPhotoPath()/images/jobseeker/20260325024136_362e5d0abd67435698e87b5ba092de6d.png
        
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