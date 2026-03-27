package com.spring.app.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.admin.domain.AdminBannerDTO;
import com.spring.app.admin.domain.AdminCommentDTO;
import com.spring.app.admin.domain.AdminCompanyDTO;
import com.spring.app.admin.domain.AdminDashboardDTO;
import com.spring.app.admin.domain.AdminJobPostDTO;
import com.spring.app.admin.domain.AdminMemberDTO;
import com.spring.app.admin.domain.AdminNoticeDTO;
import com.spring.app.admin.domain.AdminPostDTO;
import com.spring.app.admin.service.AdminBannerService;
import com.spring.app.admin.service.AdminCompanyService;
import com.spring.app.admin.service.AdminDashboardService;
import com.spring.app.admin.service.AdminJobPostService;
import com.spring.app.admin.service.AdminMemberService;
import com.spring.app.admin.service.AdminNoticeService;
import com.spring.app.admin.service.AdminPostService;
import com.spring.app.admin.service.AdminReportService;
import com.spring.app.admin.service.AdminStatisticsservice;
import com.spring.app.admin.service.AdminStatsService;
import com.spring.app.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController { 
	//private final 0327 확인
	private final AdminCompanyService companyAdminService;
	private final AdminMemberService memberAdminService;
	private final AdminJobPostService adminJobPostService;
	private final AdminNoticeService noticeAdminService;
	private final AdminBannerService bannerAdminService;
	private final AdminReportService adminReportService;
	private final AdminPostService adminPostService;
	private final AdminStatsService adminStatsService;
	private final AdminStatisticsservice adminStatisticsservice;
	private final AdminDashboardService adminDashboardService;
	private final NotificationService notificationService;
	
	/*
	@GetMapping("/dashboard")
	public String dashboard() {
		
		return "company/dashboard";
	}
	 */
	
	/* 확인용 */
	
	
	@GetMapping({"/dashboard", "/admin_dashboard"})
	public String dashboard(@RequestParam(value="menu", defaultValue="dashboard") String menu,
	                        Model model) {

	    AdminDashboardDTO dto = adminDashboardService.getDashboardData();

	    // 상단 통계
	    model.addAttribute("totalMemberCount",     dto.getTotalMemberCount());
	    model.addAttribute("jobseekerCount",        dto.getJobseekerCount());
	    model.addAttribute("companyCount",          dto.getCompanyCount());
	    model.addAttribute("activeJobPostingCount", dto.getActiveJobPostingCount());
	    model.addAttribute("todayJobPostingCount",  dto.getTodayJobPostingCount());
	    model.addAttribute("todayNewMemberCount",   dto.getTodayNewMemberCount());
	    model.addAttribute("dailyApplicationStats", dto.getDailyApplicationStats());
	    
	    // 차트 (List 그대로 전달 - HTML에서 Thymeleaf inline으로 처리)
	    model.addAttribute("dailyStats", dto.getDailyMemberStats());

	    // 알림 / 테이블
	    model.addAttribute("pendingCompanies",  dto.getPendingCompanies());
	    model.addAttribute("recentJobPostings", dto.getRecentJobPostings());   
	    model.addAttribute("recentPayments",    dto.getRecentPayments());
	    model.addAttribute("pendingJobReportCount",     dto.getPendingJobReportCount());
	    model.addAttribute("pendingPostReportCount",     dto.getPendingPostReportCount());
	    model.addAttribute("pendingCommentReportCount",  dto.getPendingCommentReportCount());
	    
	    model.addAttribute("activeMenu", menu);
	    return "admin/admin_dashboard";
	}

	// =============================================
	
	@GetMapping("/members")
	public String memberManage(
	        @RequestParam(value="menu", defaultValue="members") String menu,
	        @RequestParam(name="search", required=false) String search,
	        @RequestParam(name="status", required=false) String status,
	        @RequestParam(value="page", defaultValue="1") int page,
	        Model model) {

	    int limit = 10; //

	    List<AdminMemberDTO> members =
	            memberAdminService.getPagedMemberList(search, status, page, limit);

	    int totalCount =
	            memberAdminService.getMemberCount(search, status);

	    int totalPages = (int) Math.ceil((double) totalCount / limit);

	    model.addAttribute("activeMenu", menu);
	    model.addAttribute("members", members);
	    model.addAttribute("totalCount", totalCount);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);

	    return "admin/admin_member";
	}

	@PostMapping("/members/{memberId}/suspend")
	@ResponseBody
	public ResponseEntity<String> suspendMember(@PathVariable("memberId") String memberId) {
	    int result = memberAdminService.suspendMember(memberId);
	    return result > 0
	        ? ResponseEntity.ok("정지 완료")
	        : ResponseEntity.badRequest().body("정지 실패");
	}

	@PostMapping("/members/{memberId}/unsuspend")
	@ResponseBody
	public ResponseEntity<String> unsuspendMember(@PathVariable("memberId") String memberId) {
	    int result = memberAdminService.unsuspendMember(memberId);
	    return result > 0
	        ? ResponseEntity.ok("정지 해제 완료")
	        : ResponseEntity.badRequest().body("정지 해제 실패");
	}
	
	@PostMapping("/members/{memberId}/withdraw")
	@ResponseBody
	public ResponseEntity<String> withdrawMember(@PathVariable("memberId") String memberId) {
	    int result = memberAdminService.withdrawMember(memberId);
	    return result > 0
	        ? ResponseEntity.ok("탈퇴 처리 완료")
	        : ResponseEntity.badRequest().body("탈퇴 처리 실패");
	}
	
	@GetMapping("/members/{memberId}/detail")
	@ResponseBody
	public AdminMemberDTO memberDetail(@PathVariable("memberId") String memberId) {
	    return memberAdminService.getMemberById(memberId);
	}
	
	// ===============================================
	
	
	@GetMapping("/companies")
	public String companies(
	        @RequestParam(value="menu", defaultValue="companies") String menu,
	        @RequestParam(name="search", required=false) String search,
	        @RequestParam(name="status", required=false) String status,
	        @RequestParam(name="approval", required=false) String approval,
	        @RequestParam(value="page", defaultValue="1") int page,
	        Model model) {

	    int limit = 10;

	    List<AdminCompanyDTO> companies =
	            companyAdminService.getPagedCompanyList(search, status, approval, page, limit);

	    int totalCount =
	            companyAdminService.getCompanyCount(search, status, approval);

	    int totalPages = (int) Math.ceil((double) totalCount / limit);

	    model.addAttribute("activeMenu", menu);
	    model.addAttribute("companies", companies);
	    model.addAttribute("totalCount", totalCount);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);

	    return "admin/admin_company";
	}
	
	@PostMapping("/companies/{memberId}/approve")
	@ResponseBody
	public ResponseEntity<String> approve(@PathVariable("memberId") String memberId) {
	    int result = companyAdminService.approveCompany(memberId);
	    return result > 0
	        ? ResponseEntity.ok("승인 완료")
	        : ResponseEntity.badRequest().body("승인 실패");
	}

	@PostMapping("/companies/{memberId}/reject")
	@ResponseBody
	public ResponseEntity<String> reject(@PathVariable("memberId") String memberId, @RequestBody Map<String, String> body) {
	    int result = companyAdminService.rejectCompany(memberId, body.get("reason"));
	    return result > 0
	        ? ResponseEntity.ok("거절 완료")
	        : ResponseEntity.badRequest().body("거절 실패");
	}

	@PostMapping("/companies/{memberId}/suspend")
	@ResponseBody
	public ResponseEntity<String> suspend(@PathVariable("memberId") String memberId) {
	    int result = companyAdminService.suspendCompany(memberId);
	    return result > 0
	        ? ResponseEntity.ok("정지 완료")
	        : ResponseEntity.badRequest().body("정지 실패");
	}

	@PostMapping("/companies/{memberId}/unsuspend")
	@ResponseBody
	public ResponseEntity<String> unsuspend(@PathVariable("memberId") String memberId) {
	    int result = companyAdminService.unsuspendCompany(memberId);
	    return result > 0
	        ? ResponseEntity.ok("정지 해제 완료")
	        : ResponseEntity.badRequest().body("정지 해제 실패");
	}		
	
	@GetMapping("/companies/{memberId}/detail")
	@ResponseBody
	public AdminCompanyDTO companyDetail(@PathVariable("memberId") String memberId) {
	    return companyAdminService.getCompanyById(memberId);
	}
	
	@PostMapping("/companies/{memberId}/reapprove")
	@ResponseBody
	public ResponseEntity<String> reapprove(
	        @PathVariable("memberId") String memberId,
	        @RequestBody Map<String, String> body) {

	    int result = companyAdminService.reapproveCompany(
	        memberId,
	        body.get("bizNo"),
	        body.get("ceoName"),
	        body.get("industryCode"),
	        body.get("companyName")
	    );
	    return result > 0
	        ? ResponseEntity.ok("재승인 완료")
	        : ResponseEntity.badRequest().body("재승인 실패");
	}
	
	
	@PatchMapping("/companies/{memberId}/update-info")
	@ResponseBody
	public ResponseEntity<String> updateCompanyInfo(
	        @PathVariable("memberId") String memberId,
	        @RequestBody Map<String, String> body) {

	    int result = companyAdminService.updateCompanyInfo(
	        memberId,
	        body.get("bizNo"),
	        body.get("ceoName"),
	        body.get("industryCode"),
	        body.get("companyName")
	    );
	    return result > 0
	        ? ResponseEntity.ok("수정 완료")
	        : ResponseEntity.badRequest().body("수정 실패");
	}
	
	
	// ===================================================
	
	
	@GetMapping("/job-posts")
	public String job(
	        @RequestParam(value="menu", defaultValue="job-posts") String menu,
	        @RequestParam(name="search", required=false) String search,
	        @RequestParam(name="status", required=false) String status,
	        @RequestParam(value="page", defaultValue="1") int page,
	        Model model) {

	    int limit = 10;

	    List<AdminJobPostDTO> jobs = adminJobPostService.getPagedJobs(search, status, page, limit);
	    int totalCount = adminJobPostService.getJobCount(search, status);  // 상단 카드용 (전체 28)
	    int filteredCount = (status == null || status.isEmpty()) 
	        ? adminJobPostService.getJobCountExcludeClosedDeleted(search)  // 전체 탭: 마감·삭제 제외
	        : totalCount;
	    int totalPages = (int) Math.ceil((double) filteredCount / limit);
	    
	    if (totalPages == 0) totalPages = 1;

	    model.addAttribute("activeMenu",   menu);
	    model.addAttribute("jobs",         jobs);
	    model.addAttribute("currentPage",  page);
	    model.addAttribute("totalPages",   totalPages);
	    model.addAttribute("totalCount",   totalCount);
	    model.addAttribute("filteredCount", filteredCount);
	    model.addAttribute("activeCount",  adminJobPostService.getJobCountByStatus("active"));
	    model.addAttribute("waitingCount", adminJobPostService.getJobCountByStatus("waiting"));
	    model.addAttribute("hiddenCount",  adminJobPostService.getJobCountByStatus("hidden"));
	    model.addAttribute("closedCount",  adminJobPostService.getJobCountByStatus("closed"));
	    model.addAttribute("deletedCount",  adminJobPostService.getJobCountByStatus("deleted")); 
	    model.addAttribute("tempCount",     adminJobPostService.getJobCountByStatus("temp"));
	    
	    
	    return "admin/admin_job_posts";
	}		
	
	@PatchMapping("/job-posts/{jobId}/visibility") 
	@ResponseBody
	public ResponseEntity<?> updateJobVisibility(
			@PathVariable("jobId") Long jobId,
	        @RequestBody Map<String, Integer> body) {

	    Integer isHidden = body.get("isHidden");
	    int result = adminJobPostService.updateJobHidden(jobId, isHidden);

	    return result > 0 ? ResponseEntity.ok().build()
	                      : ResponseEntity.badRequest().build();
	}
	
	
	// =========================================
		
	@GetMapping("/banners")
	public String banners(@RequestParam(value="menu", defaultValue="banners") String menu,
	                      @RequestParam(value="page", defaultValue="1") int page,
	                      @RequestParam(value="status", required=false) String status,
	                      Model model) {
	    int limit = 10;
	    List<AdminBannerDTO> banners = bannerAdminService.getBannerList(page, limit, status);
	    int totalCount = bannerAdminService.getBannerCount(status);
	    int totalPages = (int) Math.ceil((double) totalCount / limit);
	    if (totalPages == 0) totalPages = 1;

	    model.addAttribute("activeMenu",    menu);
	    model.addAttribute("banners",       banners);
	    model.addAttribute("totalCount",	bannerAdminService.getBannerTotalCount());
	    model.addAttribute("filteredCount", bannerAdminService.getBannerCount(status));
	    model.addAttribute("pendingCount",  bannerAdminService.getBannerCountByStatus("처리중"));
	    model.addAttribute("activeCount",   bannerAdminService.getBannerCountByStatus("승인완료"));
	    model.addAttribute("rejectedCount", bannerAdminService.getBannerCountByStatus("반려"));
	    model.addAttribute("stoppedCount",  bannerAdminService.getBannerCountByStatus("정지"));
	    model.addAttribute("closedCount",   bannerAdminService.getBannerCountByStatus("마감"));
	    model.addAttribute("deletedCount",  bannerAdminService.getBannerCountByStatus("삭제됨"));
	    model.addAttribute("currentPage",   page);
	    model.addAttribute("totalPages",    totalPages);
	    model.addAttribute("currentStatus", status);

	    return "admin/admin_banners";
	}
	
	@PostMapping("/banners/{bannerId}/approve")
	@ResponseBody
	public ResponseEntity<String> approveBanner(@PathVariable("bannerId") Long bannerId) {
	    int result = bannerAdminService.approveBanner(bannerId);
	    return result > 0
	        ? ResponseEntity.ok("승인 완료")
	        : ResponseEntity.badRequest().body("승인 실패");
	}
	/* 0320 merge 확인 */
	@PostMapping("/banners/{bannerId}/reject")
	@ResponseBody
	public ResponseEntity<String> rejectBanner(@PathVariable("bannerId") Long bannerId,
	                                           @RequestBody Map<String, String> body) {
	    int result = bannerAdminService.rejectBanner(bannerId, body.get("reason"));
	    return result > 0
	        ? ResponseEntity.ok("거절 완료")
	        : ResponseEntity.badRequest().body("거절 실패");
	}

	@PostMapping("/banners/{bannerId}/stopped")
	@ResponseBody
	public ResponseEntity<String> stoppedBanner(@PathVariable("bannerId") Long bannerId) {
	    int result = bannerAdminService.stoppedBanner(bannerId);
	    return result > 0
	        ? ResponseEntity.ok("정지 완료")
	        : ResponseEntity.badRequest().body("정지 실패");
	}

	@PostMapping("/banners/{bannerId}/unstopped")
	@ResponseBody
	public ResponseEntity<String> unstoppedBanner(@PathVariable("bannerId") Long bannerId) {
	    int result = bannerAdminService.unstoppedBanner(bannerId);
	    return result > 0
	        ? ResponseEntity.ok("재승인 완료")
	        : ResponseEntity.badRequest().body("재승인 실패");
	}
	
	
	// ==========================================
	
	@GetMapping("/Statistics")
	public String Statistics(@RequestParam(value="menu", defaultValue="Statistics") String menu,
							Model model) {
			model.addAttribute("activeMenu", menu);
			return "admin/admin_Statistics";
	}
	
	

	@GetMapping("/stats")
	public String stats(@RequestParam(value="menu", defaultValue="stats") String menu,
	                    Model model) throws Exception {

	    // ── 회원 통계 ──────────────────────────
	    model.addAttribute("totalCount",    adminStatsService.getTotalMemberCount());
	    model.addAttribute("jobSeeker",     adminStatsService.getJobSeekerCount());
	    model.addAttribute("companyMember", adminStatsService.getCompanyMemberCount());
	    model.addAttribute("joinCount",     adminStatsService.getThisMonthJoinCount());
	    model.addAttribute("withdrawCount", adminStatsService.getThisMonthWithdrawCount());
	    model.addAttribute("netCount",      adminStatsService.getThisMonthJoinCount()
	                                      - adminStatsService.getThisMonthWithdrawCount());
	    model.addAttribute("joinJson",      adminStatsService.getDailyJoinLast30());
	    model.addAttribute("withdrawJson",  adminStatsService.getDailyWithdrawLast30());

	    // ── 채용공고 통계 ──────────────────────
	    model.addAttribute("activeJobCount",     adminStatsService.getActiveJobCount());
	    model.addAttribute("thisMonthJobCount",  adminStatsService.getThisMonthJobCount());
	    model.addAttribute("thisMonthClosedJob", adminStatsService.getThisMonthClosedJobCount());
	    
	 // 수정
	    model.addAttribute("jobCountActive",  adminStatsService.getJobCountByStatus(0));  // 활성
	    model.addAttribute("jobCountHidden",  adminStatsService.getJobCountByStatus(1));  // 숨김
	   
	    model.addAttribute("jobCountClosed", adminStatsService.getClosedJobCount());
	    
	    model.addAttribute("dailyJobReg",        adminStatsService.getDailyJobRegLast30());
	    model.addAttribute("dailyJobClosed",     adminStatsService.getDailyJobClosedLast30());

	    // ── 게시판 통계 ────────────────────────
	    model.addAttribute("totalPostCount",     adminStatsService.getTotalPostCount());
	    model.addAttribute("thisMonthPostCount", adminStatsService.getThisMonthPostCount());
	    model.addAttribute("hiddenPostCount",    adminStatsService.getHiddenPostCount());
	    model.addAttribute("dailyPost",          adminStatsService.getDailyPostLast30());
	    model.addAttribute("postByBoard",        adminStatsService.getPostCountByBoard());
	    model.addAttribute("inactiveBoardCount", adminStatsService.getInactiveBoardCount());
	    
	    model.addAttribute("activeMenu", menu);
	    return "admin/admin_stats";
	}
	
	@GetMapping("/stats/chart-data")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> chartData(
	        @RequestParam(value="from",   required=false) String from,
	        @RequestParam(value="to",     required=false) String to,
	        @RequestParam(value="period", defaultValue="daily") String period) {
	    try {
	        if (from == null || from.isEmpty()) {
	            java.time.LocalDate toDate   = java.time.LocalDate.now();
	            java.time.LocalDate fromDate = toDate.minusDays(30);
	            from = fromDate.toString();
	            to   = toDate.toString();
	        }
	        return ResponseEntity.ok(adminStatisticsservice.getChartData(from, to, period));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().build();
	    }
	}

	//============================================================
	//============================================================
	
	@GetMapping("/notices")
	public String notices(
	        @RequestParam(value="menu", defaultValue="notices") String menu,
	        @RequestParam(name="search", required=false) String search,
	        @RequestParam(value="page", defaultValue="1") int page,
	        Model model) {

	    int limit      = 10;
	    int totalCount = noticeAdminService.getNoticeCount(search);
	    int totalPages = (int) Math.ceil((double) totalCount / limit);
	    if (totalPages == 0) totalPages = 1;

	    model.addAttribute("activeMenu",     menu);
	    model.addAttribute("notices",        noticeAdminService.getPagedNotices(search, page, limit));
	    model.addAttribute("totalCount",     totalCount);
	    model.addAttribute("importantCount", noticeAdminService.getNoticeCountByPinned("Y"));
	    model.addAttribute("activeCount",    noticeAdminService.getNoticeCountByStatus(1));
	    model.addAttribute("popupCount",     noticeAdminService.getPopupCount());
	    model.addAttribute("currentPage",    page);
	    model.addAttribute("totalPages",     totalPages);
	    return "admin/admin_notices";
	}

	@PostMapping("/notices")
	@ResponseBody
	public ResponseEntity<?> createNotice(@RequestBody AdminNoticeDTO dto) {
	    int result = noticeAdminService.createNotice(dto);
	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}
	
	
	@GetMapping("/notices/{id}/detail")
	@ResponseBody
	public AdminNoticeDTO noticeDetail(@PathVariable("id") Long id) {
	    return noticeAdminService.getNoticeById(id);
	}

	@PutMapping("/notices/{id}")
	@ResponseBody
	public ResponseEntity<?> updateNotice(@PathVariable("id") Long id, @RequestBody AdminNoticeDTO dto) {
	    dto.setNoticeId(id);
	    int result = noticeAdminService.updateNotice(dto);
	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}

	@DeleteMapping("/notices/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteNotice(@PathVariable("id") Long id) {
	    int result = noticeAdminService.deleteNotice(id);
	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}

	@PatchMapping("/notices/{id}/type")
	@ResponseBody
	public ResponseEntity<?> toggleType(@PathVariable("id") Long id) {
	    int result = noticeAdminService.togglePinnedYn(id);
	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}

	@PatchMapping("/notices/{id}/popup")
	@ResponseBody
	public ResponseEntity<?> updatePopup(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
	    String action = body.get("action");
	    int result = "activate".equals(action)
	        ? noticeAdminService.activatePopup(id)
	        : noticeAdminService.deactivatePopup(id);
	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}
	
	// ==================================================
	

	// 메서드 교체
	@GetMapping("/reports")
	public String reports(
	        @RequestParam(value="menu", defaultValue="reports") String menu,
	        @RequestParam(name="search", required=false) String search,
	        @RequestParam(name="type",   required=false) String type,
	        @RequestParam(name="reason", required=false) String reason,
	        @RequestParam(name="status", required=false) String status,
	        @RequestParam(value="page",  defaultValue="1") int page,
	        Model model) {

	    int limit = 10;
	    int totalCount = adminReportService.getReportCount(search, type, reason, status);
	    int totalPages = (int) Math.ceil((double) totalCount / limit);
	    if (totalPages == 0) totalPages = 1;

	    model.addAttribute("activeMenu",   menu);
	    model.addAttribute("reports",      adminReportService.getPagedReports(search, type, reason, status, page, limit));
	    model.addAttribute("totalCount",   totalCount);
	    model.addAttribute("jobCount",     adminReportService.getReportCountByType(1));
	    model.addAttribute("postCount",    adminReportService.getReportCountByType(2));
	    model.addAttribute("commentCount", adminReportService.getReportCountByType(3));
	    model.addAttribute("currentPage",  page);
	    model.addAttribute("totalPages",   totalPages);

	    return "admin/admin_reports";
	}

	@PatchMapping("/reports/{reportId}/status")
	@ResponseBody
	public ResponseEntity<?> updateReportStatus(
	        @PathVariable("reportId") Long reportId,
	        @RequestBody Map<String, String> body) {

		// 변경
		String status = body.get("status");   // "승인" or "반려"
		String reason = body.get("reason");
		int result = adminReportService.updateProcessStatus(reportId, status, reason);

	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}
	
	// ==================================================

	@GetMapping("/posts")
	public String posts(
	        @RequestParam(value="menu", defaultValue="posts") String menu,
	        @RequestParam(name="search", required=false) String search,
	        @RequestParam(name="status", required=false) String status,
	        @RequestParam(value="page", defaultValue="1") int page,
	        Model model) {

	    Integer isHidden = null;
	    if ("active".equals(status))      isHidden = 0;
	    else if ("hidden".equals(status)) isHidden = 1;

	    int limit = 10;

	    List<AdminPostDTO> posts;
	    int filteredCount;

	    if ("deleted".equals(status)) {
	        posts = adminPostService.getDeletedPagedPosts(search, page, limit);
	        filteredCount = adminPostService.getDeletedPostCount();
	    } else {
	        posts = adminPostService.getPagedPosts(search, isHidden, page, limit);
	        filteredCount = adminPostService.getPostCount(search, isHidden);
	    }

	 // 변경
	    int displayCount;
	    if ("deleted".equals(status)) {
	        displayCount = filteredCount;
	    } else if (status == null || status.isEmpty()) {
	        displayCount = adminPostService.getPostCount(search, null); 
	    } else {
	        // 활성/숨김 개별 필터: filteredCount 그대로
	        displayCount = filteredCount;
	    }

	    int totalPages = (int) Math.ceil((double) displayCount / limit);
	    if (totalPages == 0) totalPages = 1;

	    model.addAttribute("activeMenu",   menu);
	    model.addAttribute("posts",        posts);
	    model.addAttribute("commentTotalAll",
	        adminPostService.getActiveCommentCount() +
	        adminPostService.getHiddenCommentCount() +
	        adminPostService.getDeletedCommentCount()
	    );
	    model.addAttribute("totalCount",
	        adminPostService.getActivePostCount() +
	        adminPostService.getHiddenPostCount() +
	        adminPostService.getDeletedPostCount()
	    );
	    model.addAttribute("filteredCount", displayCount);
	    model.addAttribute("activeCount",  adminPostService.getActivePostCount());
	    model.addAttribute("hiddenCount",  adminPostService.getHiddenPostCount());
	    model.addAttribute("deletedCount", adminPostService.getDeletedPostCount());
	    model.addAttribute("currentPage",  page);
	    model.addAttribute("totalPages",   totalPages);

	    int commentTotal;
	    int commentDisplayCount;
	    List<AdminCommentDTO> comments;
	    if ("deleted".equals(status)) {
	        commentTotal = adminPostService.getDeletedCommentCount();
	        commentDisplayCount = commentTotal;
	        comments = adminPostService.getDeletedPagedComments(search, page, limit);
	    } else {
	        commentTotal = adminPostService.getCommentCount(search, isHidden);
	        comments = adminPostService.getPagedComments(search, isHidden, page, limit);
	        
	        if (status == null || status.isEmpty()) {
	            // 전체 탭: 삭제됨 제외
	        	commentDisplayCount = adminPostService.getCommentCount(search, null);
	        } else {
	            // 활성/숨김 개별 필터: 그대로
	            commentDisplayCount = commentTotal;
	        }
	    }
	    int commentPages = (int) Math.ceil((double) commentDisplayCount / limit);
	    if (commentPages == 0) commentPages = 1;
	    
	    model.addAttribute("commentDisplayCount", commentDisplayCount);
	    model.addAttribute("comments",       comments);
	    model.addAttribute("commentTotal",   commentTotal);
	    model.addAttribute("commentActive",  adminPostService.getActiveCommentCount());
	    model.addAttribute("commentHidden",  adminPostService.getHiddenCommentCount());
	    model.addAttribute("commentDeleted", adminPostService.getDeletedCommentCount());
	    model.addAttribute("commentPages",   commentPages);

	    return "admin/admin_posts";
	}

	@PatchMapping("/posts/{postId}/visibility")
	@ResponseBody
	public ResponseEntity<?> updatePostVisibility(
	        @PathVariable("postId") Long postId,
	        @RequestBody Map<String, Integer> body) {

	    int result = adminPostService.updatePostHidden(postId, body.get("isHidden"));
	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}

	@PatchMapping("/comments/{commentId}/visibility")
	@ResponseBody
	public ResponseEntity<?> updateCommentVisibility(
	        @PathVariable("commentId") Long commentId,
	        @RequestBody Map<String, Integer> body) {

	    int result = adminPostService.updateCommentHidden(commentId, body.get("isHidden"));
	    return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
	}
	
	@ModelAttribute
	public void addNotifications(Authentication authentication, Model model) {
	    if (authentication == null || authentication.getName() == null) return;
	    
	    String memberId = authentication.getName();
	    if (memberId.equals("anonymousUser")) return;
	    
	    model.addAttribute("notificationList", notificationService.getMyNotifications(memberId));
	    model.addAttribute("unreadNotificationCount", notificationService.getUnreadNotificationCount(memberId));
	}
}
	
	
