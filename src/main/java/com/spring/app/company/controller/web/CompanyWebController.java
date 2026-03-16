package com.spring.app.company.controller.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.app.common.domain.EducationDTO;
import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.company.domain.BannerListDTO;
import com.spring.app.company.domain.CompanyDashboardDTO;
import com.spring.app.company.domain.CompanyProfileDTO;
import com.spring.app.company.domain.CompanyTopbarDTO;
import com.spring.app.company.domain.JobPostingDTO;
import com.spring.app.company.domain.MemberSimpleDTO;
import com.spring.app.company.domain.OfferListDTO;
import com.spring.app.company.domain.TalentResumeDetailDTO;
import com.spring.app.company.domain.TalentSearchConditionDTO;
import com.spring.app.company.service.CompanyService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * SSR(Thymeleaf) 전용 컨트롤러: 화면 렌더링/페이지 이동/폼 submit 담당
 * API(JSON/AJAX) 는 com.spring.app.company.controller.api 패키지로 분리한다.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyWebController {

    private final CompanyService service;
    
    //포트원 결제를 위한 객체주입
    @Value("${portone.impCode}")
    private String impCode;
    
    
    
    
    //기업 상단바 조회(기업ID, 기업명, 이메일)
    @ModelAttribute
    public void addCompanyTopbarInfo(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return;
        }

        String memberId = authentication.getName();
        CompanyTopbarDTO topbarInfo = service.getCompanyTopbarInfo(memberId);

        if (topbarInfo == null) {
            model.addAttribute("loginCompanyName", "기업명 미설정");
            model.addAttribute("loginCompanyEmail", "이메일 미등록");
            model.addAttribute("loginCompanyInitial", "C");
            return;
        }

        String companyName = topbarInfo.getCompanyName();
        String email = topbarInfo.getEmail();

        model.addAttribute("loginCompanyName",
                (companyName != null && !companyName.isBlank()) ? companyName : "기업명 미설정");

        model.addAttribute("loginCompanyEmail",
                (email != null && !email.isBlank()) ? email : "이메일 미등록");

        String initial = "C";
        if (companyName != null && !companyName.isBlank()) {
            initial = companyName.substring(0, 1);
        }

        model.addAttribute("loginCompanyInitial", initial);
    }
    
    
    
    
    // 기업용 페이지 기본
    @GetMapping({"", "/"})
    public String company() {
        return "redirect:/company/company_dashboard";
    }

    // 기업 대시보드 페이지
    @GetMapping("/company_dashboard")
    public String dashboard(@RequestParam(value = "menu", defaultValue = "company_dashboard") String menu,
                            Model model,
                            Authentication authentication) {
        model.addAttribute("activeMenu", menu);
        
        String memberId = authentication.getName(); // 로그인한 기업 회원 아이디
        
        // 대시보드 전체 데이터 조회
        CompanyDashboardDTO dashboard = service.getCompanyDashboard(memberId);
        //System.out.println(dashboard.getRecentApplicants());
        /*
        [DashboardApplicantDTO(applicationId=13, applicantName=구직자, resumeTitle=경력이력서, jobTitle=디자이너 급구, appliedAt=Sat Mar 14 17:48:05 KST 2026, processStatus=0, processStatusText=지원완료), 
        DashboardApplicantDTO(applicationId=12, applicantName=구직자, resumeTitle=경력이력서, jobTitle=디자이너 급구, appliedAt=Fri Mar 13 21:05:22 KST 2026, processStatus=0, processStatusText=지원완료), 
        DashboardApplicantDTO(applicationId=11, applicantName=구직자, resumeTitle=경력이력서, jobTitle=디자이너 급구, appliedAt=Fri Mar 13 20:51:31 KST 2026, processStatus=0, processStatusText=지원완료), 
        DashboardApplicantDTO(applicationId=10, applicantName=구직자, resumeTitle=경력이력서, jobTitle=디자이너 급구, appliedAt=Fri Mar 13 18:54:12 KST 2026, processStatus=0, processStatusText=지원완료), 
        DashboardApplicantDTO(applicationId=9, applicantName=구직자, resumeTitle=경력이력서, jobTitle=디자이너 급구, appliedAt=Fri Mar 13 18:06:09 KST 2026, processStatus=0, processStatusText=지원완료)]
        */
        
        model.addAttribute("dashboard", dashboard);
        
        return "company/company_dashboard";
    }

    
    
    // ============================== 프로필 ============================== //
    // 기업 프로필 페이지
    @GetMapping("/profile")
    public String profile(@RequestParam(value = "tab", defaultValue = "basic") String tab,
                          Model model, Authentication authentication) {
        model.addAttribute("activeMenu", "profile");
        model.addAttribute("activeTab", tab);
        
        String memberId = authentication.getName();
        
        // 로그인 기업 회원의 프로필 조회
        CompanyProfileDTO profile = service.getCompanyProfile(memberId);
        model.addAttribute("profile", profile);
        
        return "company/profile";
    }
    // ============================== 프로필 ============================== //
    
    
    
    

    // ============================== 채용공고 ============================== //
    // 채용공고 리스트 페이지
    /*
    @GetMapping("/job/list")
    public String jobs(@RequestParam(value = "jobId", required = false) Long jobId,
                       Model model,
                       Authentication authentication) {

        model.addAttribute("activeMenu", "job");

        service.refreshJobPostingStatuses();
        
        String memberId = authentication.getName();
        // 채용공고 리스트 조회
        List<JobPostingDTO> jobList = service.getJobPostingList(memberId);
        model.addAttribute("jobList", jobList);

        // 선택된 공고 상세정보 조회
        if (jobId != null) {
            JobPostingDTO selectedJob = service.getJobPostingOne(jobId);
            model.addAttribute("selectedJob", selectedJob);
        }

        return "company/job/job_list";
    }
    */
    
    //채용공고 리스트 조회(페이징처리)
    @GetMapping("/job/list")
    public String jobs(@RequestParam(value = "jobId", required = false) Long jobId,
                       @RequestParam(value = "currentShowPageNo", required = false, defaultValue = "1") String currentShowPageNo,
                       Model model,
                       Authentication authentication) {

        model.addAttribute("activeMenu", "job");

        service.refreshJobPostingStatuses();

        String memberId = authentication.getName();

        // 한 페이지당 보여줄 개수
        int sizePerPage = 5;

        // 전체 공고 수
        int totalCount = service.getJobPostingCount(memberId);

        // 전체 페이지 수
        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);

        int pageNo;
        try {
            pageNo = Integer.parseInt(currentShowPageNo);
            if (pageNo < 1) {
                pageNo = 1;
            }
            if (totalPage > 0 && pageNo > totalPage) {
                pageNo = totalPage;
            }
        } catch (NumberFormatException e) {
            pageNo = 1;
        }

        int startRow = ((pageNo - 1) * sizePerPage) + 1;
        int endRow = startRow + sizePerPage - 1;

        java.util.Map<String, Object> paraMap = new java.util.HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("startRow", startRow);
        paraMap.put("endRow", endRow);

        // 페이징된 공고 리스트 조회
        List<JobPostingDTO> jobList = service.getJobPostingListPaing(paraMap);
        model.addAttribute("jobList", jobList);

        // 전체 개수 / 페이지 정보
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("sizePerPage", sizePerPage);
        model.addAttribute("currentShowPageNo", pageNo);
        model.addAttribute("totalPage", totalPage);

        // 선택된 공고 상세정보 조회
        if (jobId != null) {
            JobPostingDTO selectedJob = service.getJobPostingOne(jobId);
            model.addAttribute("selectedJob", selectedJob);
        }

        return "company/job/job_list";
    }

    // 채용공고 등록 페이지
    @GetMapping("/job/job_write")
    public String jobWriteForm(Model model) {
        model.addAttribute("activeMenu", "job");

        // 학력 리스트
        List<EducationDTO> eduDtoList = service.selectEduList();
        model.addAttribute("eduDtoList", eduDtoList);

        // 직무 대분류(depth=0)
        List<JobCategoryDTO> cat1List = service.getRoots();
        model.addAttribute("cat1List", cat1List);

        // 기술스택 카테고리+스킬
        model.addAttribute("skillCategoryList", service.getSkillCategoryWithSkills());

        // 레벨1 지역(시/도)
        model.addAttribute("region1List", service.getRegionLevel1());

        return "company/job/job_write";
    }


	// 채용공고 수정 페이지 (job_write 템플릿 재사용 형태)
	@GetMapping("/job/update")
	public String jobUpdateForm(@RequestParam("jobId") long jobId, Model model) {
	    model.addAttribute("activeMenu", "job");
	    model.addAttribute("jobId", jobId);
	
	    // 등록 페이지와 동일한 lookup 데이터 제공
	    model.addAttribute("eduDtoList", service.selectEduList());
	    model.addAttribute("cat1List", service.getRoots());
	    model.addAttribute("skillCategoryList", service.getSkillCategoryWithSkills());
	    model.addAttribute("region1List", service.getRegionLevel1());
	
	    return "company/job/job_update";
	}
    // ============================== 채용공고 ============================== //

    
    
    
	// ============================== 지원자 관리 ============================== //
    // 지원자 관리 리스트 페이지
    @GetMapping("/applicant/list")
    public String applicants(Model model, Authentication authentication) {
        model.addAttribute("activeMenu", "applicant");
        
        String memberId = authentication.getName();
        
        //공고 필터용 목록
        model.addAttribute("jobList", service.getJobPostingList(memberId));
        
        return "company/applicant/applicant_list";
    }
    // ============================== 지원자 관리 ============================== //
    
    

    
    
    // ============================== 제안서 ============================== //
    // 제안서 관리 리스트 페이지
    @GetMapping("/offer/list")
    public String offers(Model model,
    					 Authentication authentication) {
        model.addAttribute("activeMenu", "offer");

        String memberId = authentication.getName();
        
        // 1. 제안서 리스트
        List<OfferListDTO> offerList = service.selectOfferList(memberId);
        model.addAttribute("offerList", offerList);

        // 2. 채용공고 목록
        List<JobPostingDTO> jobList = service.getJobPostingList(memberId);
        model.addAttribute("jobList", jobList);

        // 3. 발송 대상 구직자 목록
        List<MemberSimpleDTO> receiverList = service.getReceiverList();
        model.addAttribute("receiverList", receiverList);

        return "company/offer/offer_list";
    }
    // ============================== 제안서 ============================== //

    
    
    
    
    
    // ============================== 배너 ============================== //
    // 배너 광고 페이지
    @GetMapping("/banner/list")
    public String ads(Model model, Authentication authentication,
    				  HttpServletRequest request) {
        model.addAttribute("activeMenu", "banner");
      //기업Id 알아오기
        String memberId = authentication.getName();
        
        List<BannerListDTO> bannerList = service.getBannerListByMemberId(memberId);
        model.addAttribute("bannerList", bannerList);
        System.out.println(bannerList);
        /*
        [BannerListDTO(bannerId=7, fkMemberId=TESTC, fkJobId=1018, title=api 적용 후 공고 등록입니다! 테스트로 기술을 수정해보겠습니다. ,imageFileId=6, startAt=2026-03-08 00:00, endAt=2026-03-15 00:00, status=승인완료, rejectReason=null, jobTitle=api 적용 후 공고 등록입니다! 테스트로 기술을 수정해보겠습니다., fileUrl=/images/banner/20260308202127_1df9fed2223a478aad831c2764272be6.jpg, originalFilename=minsoocap.jpg), 
        BannerListDTO(bannerId=6, fkMemberId=TESTC, fkJobId=1013, title=김스트 공고 모집입니다!!!, imageFileId=5, startAt=2026-03-08 00:00, endAt=2026-03-15 00:00, status=승인완료, rejectReason=null, jobTitle=테스트 공고 모집입니다!!!, fileUrl=/images/banner/20260308193446_7eb2f75ca1ee4573baba68caae4594e4.jpg, originalFilename=minsooyellow.jpg), 
        BannerListDTO(bannerId=5, fkMemberId=TESTC, fkJobId=1015, title=세 번째 테스트 공고등록, imageFileId=4, startAt=2026-03-08 00:00, endAt=2026-03-15 00:00, status=반려, rejectReason=죄송합니다., jobTitle=세 번째 테스트 공고등록, fileUrl=/images/banner/20260308191118_e28c59ba30474b54a0a0867819cebc52.png, originalFilename=스크린샷 2025-12-29 231208.png), 
        BannerListDTO(bannerId=4, fkMemberId=TESTC, fkJobId=1022, title=임시상태에 대한 공고입니다, imageFileId=3, startAt=2026-03-07 00:00, endAt=2026-03-14 00:00, status=반려, rejectReason=테스트 거절입니다., jobTitle=임시상태에 대한 공고입니다, fileUrl=/images/banner/20260307213000_106734c083f04c0793b2d01c0f20afa4.png, originalFilename=KakaoTalk_20260220_124013670.png), 
        BannerListDTO(bannerId=3, fkMemberId=TESTC, fkJobId=1023, title=임시저장 두 번째, imageFileId=2, startAt=2026-03-06 00:00, endAt=2026-03-13 00:00, status=승인완료, rejectReason=null, jobTitle=임시저장 두 번째, fileUrl=/file_images/banner/20260306170938_c450a100645a41cb8583f0df3d70eca7.jpg, originalFilename=blue.jpg)]
        */
        model.addAttribute("contextPath", request.getContextPath());
        
        return "company/banner/banner_list";
    }

    //배너 등록 페이지
    @GetMapping("/banner/write")
    public String bannerWriteForm(Model model, 
    							  Authentication authentication) {
        model.addAttribute("activeMenu", "banner");
        
        return "company/banner/banner_write";
    }
    // ============================== 배너 ============================== //
    
    
    
    
    // ============================== 포인트-지갑 ============================== //
    //포인트 & 지갑 페이지
    @GetMapping("/wallet")
    public String wallet(@RequestParam(value="menu", defaultValue="wallet") String menu,
                         @RequestParam(value="tab", defaultValue="wallet") String tab,
                         Authentication authentication,
                         Model model) {
    	
        model.addAttribute("activeMenu", menu);
        model.addAttribute("activeTab", tab);

        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }

        //기업ID
        String memberId = authentication.getName();

        //PortOne IMP.init용
        model.addAttribute("impCode", impCode);

        //tab 값으로 필요한 데이터만 내려주는 구조 유지
        model.addAllAttributes(service.getWalletPageData(memberId, tab));

        return "company/wallet";
    }
    // ============================== 포인트-지갑 ============================== //
    
    


    // 인재검색 페이지
    @GetMapping("/talent")
    public String talent(@RequestParam(value = "menu", defaultValue = "talent") String menu,
    					 @ModelAttribute TalentSearchConditionDTO searchDto,
                         Model model) {
        model.addAttribute("activeMenu", menu);
        
        // 필터 데이터
        model.addAttribute("jobCategoryList", service.getJobCategoryList());
        model.addAttribute("skillCategoryList", service.getSkillCategoryList());
        model.addAttribute("skillList", service.getSkillList());
        
        
        // 공개 대표이력서 목록
        model.addAttribute("resumeList", service.getPublicPrimaryResumeList(searchDto));
        model.addAttribute("totalCount", service.getPublicPrimaryResumeCount(searchDto));
        model.addAttribute("searchDto", searchDto);
        
        return "company/talent/talent_search";
    }
    
    // 공개 이력서 상세
    @GetMapping("/talent/detail")
    public String talentDetail(@RequestParam("resumeId") Long resumeId,
                               Model model) {
    	model.addAttribute("activeMenu", "talent");

        TalentResumeDetailDTO dto = service.getPublicPrimaryResumeDetail(resumeId);

        if (dto == null) {
            return "redirect:/company/talent/talent?menu=talent";
        }

        model.addAttribute("resume", dto);

        return "company/talent/talent_detail";
    }
    
    
}
