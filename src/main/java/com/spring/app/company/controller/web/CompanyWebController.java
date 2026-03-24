package com.spring.app.company.controller.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.service.NotificationService;

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
    private final NotificationService notificationService;
    
    //포트원 결제를 위한 객체주입
    @Value("${portone.impCode}")
    private String impCode;
    
    
    
    
    //기업 상단바 조회(기업ID, 기업명, 이메일)
    @ModelAttribute
    public void addCompanyTopbarInfo(Model model, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            model.addAttribute("notificationList", java.util.Collections.emptyList());
            model.addAttribute("unreadNotificationCount", 0);
            return;
        }

        String memberId = authentication.getName();

        // ===== 기업 상단바 정보 =====
        CompanyTopbarDTO topbarInfo = service.getCompanyTopbarInfo(memberId);

        if (topbarInfo == null) {
            model.addAttribute("loginCompanyName", "기업명 미설정");
            model.addAttribute("loginCompanyEmail", "이메일 미등록");
            model.addAttribute("loginCompanyInitial", "C");
        }
        else {
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

        // ===== 알림 정보 =====
        List<NotificationDTO> notificationList = notificationService.getMyNotifications(memberId);
        int unreadNotificationCount = notificationService.getUnreadNotificationCount(memberId);

        model.addAttribute("notificationList", notificationList);
        model.addAttribute("unreadNotificationCount", unreadNotificationCount);
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
        /*
        if (jobId != null) {
            JobPostingDTO selectedJob = service.getJobPostingOne(jobId);
            model.addAttribute("selectedJob", selectedJob);
        }
        */
        // 선택된 공고 상세정보 조회(로그인 한 기업 계정까지 2차 체크)
        if (jobId != null) {
            JobPostingDTO selectedJob = service.getJobPostingOne(jobId);

            if (selectedJob != null && memberId.equals(selectedJob.getMemberId())) {
                model.addAttribute("selectedJob", selectedJob);
            }
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
    // URL 직접 입력으로 페이지 접속 방지 추가
    @GetMapping("/job/update")
    public String jobUpdateForm(@RequestParam("jobId") long jobId,
                                Model model,
                                Authentication authentication) {
        model.addAttribute("activeMenu", "job");

        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }

        String memberId = authentication.getName();
        JobPostingDTO post = service.getJobPostingOne(jobId);

        if (post == null || !memberId.equals(post.getMemberId())) {
            return "redirect:/company/job/list";
        }

        // 신고 공고는 수정 페이지 진입 자체를 막음
        if (post.getIsHidden() != null && post.getIsHidden() == 1) {
            return "redirect:/company/job/list";
        }

        model.addAttribute("jobId", jobId);
        model.addAttribute("eduDtoList", service.selectEduList());
        model.addAttribute("cat1List", service.getRoots());
        model.addAttribute("skillCategoryList", service.getSkillCategoryWithSkills());
        model.addAttribute("region1List", service.getRegionLevel1());

        return "company/job/job_update";
    }
	
	
	//공고상세 팝업창 열기
	@GetMapping("/job/detail/{jobId}")
	public String jobDetailPopup(@PathVariable("jobId") Long jobId,
	                             Model model,
	                             Authentication authentication) {

	    model.addAttribute("activeMenu", "job");

	    if (authentication == null || authentication.getName() == null) {
	        return "redirect:/login";
	    }

	    String memberId = authentication.getName();
	    service.refreshJobPostingStatuses();

	    JobPostingDTO post = service.getJobPostingOne(jobId);

	    // 공고가 없거나, 로그인한 기업의 공고가 아니면 목록으로
	    if (post == null || !memberId.equals(post.getMemberId())) {
	        return "redirect:/company/job/list";
	    }

	    model.addAttribute("post", post);
	    return "company/job/job_detail_popup";
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
                         Authentication authentication,
                         @RequestParam(value = "targetMemberId", required = false) String targetMemberId,
                         @RequestParam(value = "targetMemberName", required = false) String targetMemberName,
                         @RequestParam(value = "targetResumeId", required = false) Long targetResumeId,
                         @RequestParam(value = "openOfferSend", defaultValue = "0") int openOfferSend) {

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

        // 4. 인재 상세에서 넘어온 자동 선택 대상
        model.addAttribute("scoutTargetMemberId", targetMemberId);
        model.addAttribute("scoutTargetMemberName", targetMemberName);
        model.addAttribute("scoutTargetResumeId", targetResumeId);
        model.addAttribute("openOfferSendFromTalent", openOfferSend == 1);

        return "company/offer/offer_list";
    }
    // ============================== 제안서 ============================== //

    
    
    
    
    
    // ============================== 배너 ============================== //
    // 배너 목록 조회 페이지(페이징 처리)
    @GetMapping("/banner/list")
    public String ads(@RequestParam(value = "currentShowPageNo", required = false, defaultValue = "1") String currentShowPageNo,
                      Model model,
                      Authentication authentication,
                      HttpServletRequest request) {

        model.addAttribute("activeMenu", "banner");

        service.refreshBannerStatuses();

        String memberId = authentication.getName();

        int sizePerPage = 5;
        int totalCount = service.getBannerCountByMemberId(memberId);
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

        if (totalPage == 0) {
            pageNo = 1;
        }

        int startRow = ((pageNo - 1) * sizePerPage) + 1;
        int endRow = startRow + sizePerPage - 1;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("startRow", startRow);
        paraMap.put("endRow", endRow);

        List<BannerListDTO> bannerList = service.getBannerListByMemberIdPaging(paraMap);

        model.addAttribute("bannerList", bannerList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("sizePerPage", sizePerPage);
        model.addAttribute("currentShowPageNo", pageNo);
        model.addAttribute("totalPage", totalPage);
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
                         @RequestParam(value="currentShowPageNo", defaultValue="1") String currentShowPageNoStr,
                         Authentication authentication,
                         Model model,
                         HttpServletRequest request) {

        model.addAttribute("activeMenu", menu);
        model.addAttribute("activeTab", tab);

        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }

        String memberId = authentication.getName();
        model.addAttribute("impCode", impCode);

        int currentShowPageNo = 1;
        try {
            currentShowPageNo = Integer.parseInt(currentShowPageNoStr);
            if (currentShowPageNo < 1) currentShowPageNo = 1;
        } catch (NumberFormatException e) {
            currentShowPageNo = 1;
        }

        int sizePerPage = 5;

        Map<String, Object> walletData = service.getWalletPageData(memberId, tab, currentShowPageNo, sizePerPage);
        model.addAllAttributes(walletData);

        int totalCount = (int) walletData.get("totalCount");
        String pageBar = buildWalletPageBar(tab, currentShowPageNo, sizePerPage, totalCount, request);

        model.addAttribute("pageBar", pageBar);

        return "company/wallet";
    }
    
    // 결제_포인트용 페이징 처리를 위한 메서드
    private String buildWalletPageBar(String tab,
            int currentShowPageNo,
            int sizePerPage,
            int totalCount,
            HttpServletRequest request) {

    	int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);
    	if (totalPage == 0) {
    		return "";
    	}
	
    	int blockSize = 10;
    	int pageNo = ((currentShowPageNo - 1) / blockSize) * blockSize + 1;
	
    	String contextPath = request.getContextPath();
    	String baseUrl = contextPath + "/company/wallet?menu=wallet&tab=" + tab + "&currentShowPageNo=";
	
    	StringBuilder pageBar = new StringBuilder();
    	pageBar.append("<div class='job-pagination-wrap'>");
    	pageBar.append("<div class='job-pagination'>");
	
    	// 이전
    	if (currentShowPageNo > 1) {
    		pageBar.append("<a class='job-page-btn' href='")
    		.append(baseUrl).append(currentShowPageNo - 1)
    		.append("'>이전</a>");
    	} else {
    		pageBar.append("<span class='job-page-btn is-disabled'>이전</span>");
    	}
	
    	// 번호
    	for (int i = 1; i <= totalPage; i++) {
    		if (i == currentShowPageNo) {
    			pageBar.append("<span class='job-page-num active'>")
    			.append(i)
    			.append("</span>");
    		} else {
    			pageBar.append("<a class='job-page-num' href='")
    			.append(baseUrl).append(i)
    			.append("'>")
    			.append(i)
    			.append("</a>");
    		}
    	}
    	
    	// 다음
    	if (currentShowPageNo < totalPage) {
    		pageBar.append("<a class='job-page-btn' href='")
    		.append(baseUrl).append(currentShowPageNo + 1)
    		.append("'>다음</a>");
    	} else {
    		pageBar.append("<span class='job-page-btn is-disabled'>다음</span>");
    	}
	
    	pageBar.append("</div>");
    	pageBar.append("</div>");
    	
    	return pageBar.toString();
	}
    
    
    // ============================== 포인트-지갑 ============================== //
    
    


    
    // ============================== 인재 검색 ============================== //
    private String buildTalentPageBar(TalentSearchConditionDTO searchDto, int totalCount, HttpServletRequest request) {
        int sizePerPage = (searchDto.getSize() == null || searchDto.getSize() < 1) ? 10 : searchDto.getSize();
        int currentShowPageNo = (searchDto.getPage() == null || searchDto.getPage() < 1) ? 1 : searchDto.getPage();

        int totalPage = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPage == 0) {
            return "";
        }

        int blockSize = 10;
        int pageNo = ((currentShowPageNo - 1) / blockSize) * blockSize + 1;
        int loop = 1;

        String baseUrl = request.getContextPath() + "/company/talent";
        String queryString = buildTalentQueryString(request);

        StringBuilder pageBar = new StringBuilder();
        pageBar.append("<div class='talent-pagebar-wrap'>");
        pageBar.append("<ul class='talent-pagebar'>");

        // 이전 블록
        if (pageNo != 1) {
            pageBar.append("<li>")
                   .append("<a href='").append(baseUrl).append("?menu=talent&page=").append(pageNo - 1).append(queryString).append("'>")
                   .append("&laquo;")
                   .append("</a>")
                   .append("</li>");
        }

        while (!(loop > blockSize || pageNo > totalPage)) {
            if (pageNo == currentShowPageNo) {
                pageBar.append("<li>")
                       .append("<span class='is-current'>").append(pageNo).append("</span>")
                       .append("</li>");
            } else {
                pageBar.append("<li>")
                       .append("<a href='").append(baseUrl).append("?menu=talent&page=").append(pageNo).append(queryString).append("'>")
                       .append(pageNo)
                       .append("</a>")
                       .append("</li>");
            }

            loop++;
            pageNo++;
        }

        // 다음 블록
        if (pageNo <= totalPage) {
            pageBar.append("<li>")
                   .append("<a href='").append(baseUrl).append("?menu=talent&page=").append(pageNo).append(queryString).append("'>")
                   .append("&raquo;")
                   .append("</a>")
                   .append("</li>");
        }

        pageBar.append("</ul>");
        pageBar.append("</div>");

        return pageBar.toString();
    }

    // 인재검색용 페이징 처리를 위한 메서드
    private String buildTalentQueryString(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        Map<String, String[]> paraMap = request.getParameterMap();

        paraMap.forEach((key, values) -> {
            // page는 pageBar에서 새로 붙이므로 제외
            if ("page".equals(key) || "menu".equals(key)) {
                return;
            }

            if (values == null) {
                return;
            }

            for (String value : values) {
                if (value == null || value.isBlank()) {
                    continue;
                }

                try {
                    sb.append("&")
                      .append(java.net.URLEncoder.encode(key, java.nio.charset.StandardCharsets.UTF_8))
                      .append("=")
                      .append(java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8));
                } catch (Exception e) {
                    // 인코딩 실패 시 해당 파라미터만 무시
                }
            }
        });

        return sb.toString();
    }
    
    
    
    // 인재검색 페이지
    @GetMapping("/talent")
    public String talent(@RequestParam(value = "menu", defaultValue = "talent") String menu,
                         @ModelAttribute TalentSearchConditionDTO searchDto,
                         Model model,
                         HttpServletRequest request) {

        model.addAttribute("activeMenu", menu);

        // 기본 페이징 값 고정
        if (searchDto.getPage() == null || searchDto.getPage() < 1) {
            searchDto.setPage(1);
        }
        searchDto.setSize(10); // 한 페이지당 10명 고정

        // 필터 데이터
        model.addAttribute("jobCategoryList", service.getJobCategoryList());
        model.addAttribute("skillCategoryList", service.getSkillCategoryList());
        model.addAttribute("skillList", service.getSkillList());

        int totalCount = service.getPublicPrimaryResumeCount(searchDto);
        int totalPage = (int) Math.ceil((double) totalCount / searchDto.getSize());

        // 요청 페이지가 총 페이지보다 크면 마지막 페이지로 보정
        if (totalPage > 0 && searchDto.getPage() > totalPage) {
            searchDto.setPage(totalPage);
        }

        model.addAttribute("resumeList", service.getPublicPrimaryResumeList(searchDto));
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("pageBar", buildTalentPageBar(searchDto, totalCount, request));

        return "company/talent/talent_search";
    }
    
    
    // 공개 이력서 상세
    @GetMapping("/talent/detail")
    public String talentDetail(@RequestParam("resumeId") Long resumeId,
                               Model model,
                               Authentication authentication) {

        model.addAttribute("activeMenu", "talent");

        TalentResumeDetailDTO dto = service.getPublicPrimaryResumeDetail(resumeId);

        if (dto == null) {
            // 조회 불가능한 공개 이력서면 인재검색 목록으로 보냄
            return "redirect:/company/talent?menu=talent";
        }

        String memberId = authentication.getName();

        // 인재 상세 페이지에서도 기존 제안서 템플릿 목록을 사용할 수 있게 내려줌
        List<OfferListDTO> offerList = service.selectOfferList(memberId);

        model.addAttribute("resume", dto);
        model.addAttribute("offerList", offerList);

        return "company/talent/talent_detail";
    }
    
    // ============================== 인재 검색 ============================== //
    
    
}
