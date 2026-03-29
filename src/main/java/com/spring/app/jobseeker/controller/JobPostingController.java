package com.spring.app.jobseeker.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.service.JobPostingService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/job")
public class JobPostingController {

	private final JobPostingService jobPostingService;

	// 쿠키 설정
	private static final String RECENT_VIEW_COOKIE = "recentViewedJobs"; // 쿠키명
	private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;         // 7일 (초 단위)
	private static final int RECENT_VIEW_MAX = 10;                        // 최대 저장 건수

	public JobPostingController(JobPostingService jobPostingService) {
		this.jobPostingService = jobPostingService;
	}

	@GetMapping("/list")
	public String list(
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "regions", required = false) List<String> regions,
			@RequestParam(value = "careers", required = false) List<String> careers,
			@RequestParam(value = "eduLevels", required = false) List<String> eduLevels,
			@RequestParam(value = "empTypes", required = false) List<String> empTypes,
			@RequestParam(value = "roles", required = false) List<String> roles,
			@RequestParam(value = "companyTypes", required = false) List<String> companyTypes,
			@RequestParam(value = "techStacks", required = false) List<String> techStacks,
			@RequestParam(value = "regionDetails", required = false) List<String> regionDetails,
			@RequestParam(value = "salaryMin", defaultValue = "0") int salaryMin,
			@RequestParam(value = "includeClosed", required = false) String includeClosed,
			@RequestParam(value = "sort", defaultValue = "latest") String sort,
			@RequestParam(value = "page", defaultValue = "1") int page,
			Authentication authentication,
			Model model) {

		// === 검색/필터 파라미터 Map 구성 === //
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("keyword", keyword);
		paraMap.put("regions", regions);
		paraMap.put("careers", careers);
		paraMap.put("eduLevels", eduLevels);
		paraMap.put("empTypes", empTypes);
		paraMap.put("roles", roles);
		paraMap.put("companyTypes", companyTypes);
		paraMap.put("techStacks", techStacks);
		paraMap.put("regionDetails", regionDetails);
		paraMap.put("salaryMin", salaryMin);
		paraMap.put("includeClosed", includeClosed);
		paraMap.put("sort", sort);

		// === 페이징 처리 === //
		int sizePerPage = 10;
		int startRow = (page - 1) * sizePerPage + 1;
		int endRow = page * sizePerPage;
		paraMap.put("startRow", startRow);
		paraMap.put("endRow", endRow);

		// === 전체 건수 조회 === //
		int totalCount = jobPostingService.getJobPostingCount(paraMap);

		// === 전체 채용공고 목록 조회 === //
		List<JobPostingListDTO> jobPosts = jobPostingService.getJobPostingList(paraMap);

		
		// ======= 추천 채용공고 + 인기 채용공고 조회 ======= //
		List<JobPostingListDTO> recommendedPosts = new ArrayList<>();
		// 인기채용공고
		List<JobPostingListDTO> popularPosts = jobPostingService.getPopularJobPostings(3);
		
		String loginId = null;
		boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());
		boolean hasResume = false;
		if (isLoggedIn) {
			loginId = authentication.getName();
			hasResume = jobPostingService.hasPrimaryResume(loginId);
			if (hasResume) {
				// 추천채용공고
				recommendedPosts = jobPostingService.getRecommendedJobPostings(loginId);
			}
		}
		model.addAttribute("isLoggedIn", isLoggedIn);
		model.addAttribute("hasResume", hasResume);
		model.addAttribute("popularPosts", popularPosts);

		// === 페이징 정보 계산 === //
		int totalPages = (int) Math.ceil((double) totalCount / sizePerPage);
		int blockSize = 5;
		int startPage = ((page - 1) / blockSize) * blockSize + 1;
		int endPage = Math.min(startPage + blockSize - 1, totalPages);

		// === Model에 데이터 전달 === //
		model.addAttribute("jobPosts", jobPosts);
		model.addAttribute("recommendedPosts", recommendedPosts);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("blockSize", blockSize);

		// 검색/필터 조건 유지용
		model.addAttribute("keyword", keyword);
		model.addAttribute("regions", regions);
		model.addAttribute("careers", careers);
		model.addAttribute("eduLevels", eduLevels);
		model.addAttribute("empTypes", empTypes);
		model.addAttribute("roles", roles);
		model.addAttribute("companyTypes", companyTypes);
		model.addAttribute("techStacks", techStacks);
		model.addAttribute("regionDetails", regionDetails);
		model.addAttribute("salaryMin", salaryMin);
		model.addAttribute("includeClosed", includeClosed);
		model.addAttribute("sort", sort);

		// === 필터용 마스터 데이터 전달 === //
		model.addAttribute("regionList", jobPostingService.getRegionList());
		model.addAttribute("eduLevelList", jobPostingService.getEduLevelList());
		model.addAttribute("categoryList", jobPostingService.getJobCategoryList());
		model.addAttribute("skillGroupList", jobPostingService.getSkillListGroupByCategory());

		return "job/list";
	}


	@GetMapping("/detail/{id}")
	public ModelAndView detail(@PathVariable("id") Long id,
	                           Authentication authentication,
	                           HttpServletRequest request,
	                           HttpServletResponse response,
	                           ModelAndView mav) {

		// === DB에서 공고 상세 조회 (조회수 증가 포함) === //
		JobPostingListDTO post = jobPostingService.getJobPostingDetail(id);

		if (post == null) {
			mav.setViewName("redirect:/job/list");
			return mav;
		}

		// === 로그인 시 DB에도 열람 기록 저장 === //
		if (authentication != null && authentication.isAuthenticated()) {
			String loginId = authentication.getName();
			jobPostingService.saveViewLog(loginId, id);
		}

		// === 쿠키에 최근본 공고 ID 저장 (로그인/비로그인 모두) === //
		saveRecentViewToCookie(request, response, id);

		// === 지원 여부 확인 === //
		boolean alreadyApplied = false;
		if (authentication != null && authentication.isAuthenticated()) {
			String loginId2 = authentication.getName();
			alreadyApplied = jobPostingService.hasAlreadyApplied(id, loginId2);
		}
		mav.addObject("alreadyApplied", alreadyApplied);

		// === 기업 탈퇴 여부 확인 === //
		boolean isCompanyWithdrawn = false;
		try {
			int companyStatus = jobPostingService.getCompanyMemberStatus(post.getMemberId());
			isCompanyWithdrawn = (companyStatus == 2);
		} catch (Exception e) {
			// 기업 탈퇴 여부 확인 실패 시 기본값(false) 유지
		}
		mav.addObject("isCompanyWithdrawn", isCompanyWithdrawn);

		// === 팔로우 상태 확인 === //
		boolean isFollowed = false;
		if (authentication != null && authentication.isAuthenticated()) {
			isFollowed = jobPostingService.isFollowed(post.getMemberId(), authentication.getName());
		}
		mav.addObject("isFollowed", isFollowed);

		// === 스크랩 상태 확인 === //
		boolean isScrapped = false;
		if (authentication != null && authentication.isAuthenticated()) {
			isScrapped = jobPostingService.isScraped(id, authentication.getName());
		}
		mav.addObject("isScrapped", isScrapped);

		// === 지원자 통계 === //
		Map<String, Object> stats = jobPostingService.getApplicantStats(id);
		int totalCount2 = stats != null ? ((Number) stats.getOrDefault("totalCount", 0)).intValue() : 0;
		if (totalCount2 > 0) {
			mav.addObject("stats", stats);
		}

		// === 신고사유 목록 === //
		mav.addObject("reportReasons", jobPostingService.getReportReasonList());

		// === 매칭도 조회 (로그인 + 대표이력서 있을 때만) === //
		if (authentication != null && authentication.isAuthenticated()) {
			String loginId3 = authentication.getName();
			Map<String, Object> matchData = jobPostingService.getMatchScoreForJob(id, loginId3);
			if (matchData != null) {
				mav.addObject("matchData", matchData);
			}
		}

		// === 유사 공고 추천 (항상 표시) === //
		List<JobPostingListDTO> similarPosts = jobPostingService.getSimilarJobPostings(id);
		mav.addObject("similarPosts", similarPosts);

		// === 공고 상태 판별 === //
		boolean isClosed = false;
		boolean isPrivate = false;
		boolean isDeleted = "삭제됨".equals(post.getStatus());

		if (!isDeleted) {
			// 비공개: is_hidden=1 또는 게시종료일 지남
			if (post.getIsHidden() == 1) {
				isPrivate = true;
			} else if (post.getClosedAt() != null && !post.getClosedAt().isEmpty()) {
				try {
					java.time.LocalDate closedDate = java.time.LocalDate.parse(post.getClosedAt(), java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
					if (closedDate.isBefore(java.time.LocalDate.now())) isPrivate = true;
				} catch (Exception e) {
					// 날짜 파싱 실패 시 기본값 유지
				}
			}

			// 마감: status=마감 또는 마감일 지남
			if ("마감".equals(post.getStatus())) {
				isClosed = true;
			} else if (!"always".equals(post.getDeadlineType()) && post.getDeadlineAt() != null && !post.getDeadlineAt().isEmpty()) {
				try {
					java.time.LocalDate deadlineDate = java.time.LocalDate.parse(post.getDeadlineAt(), java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));
					if (deadlineDate.isBefore(java.time.LocalDate.now())) isClosed = true;
				} catch (Exception e) {
					// 날짜 파싱 실패 시 기본값 유지
				}
			}
		}
		mav.addObject("isClosed", isClosed);
		mav.addObject("isPrivate", isPrivate);
		mav.addObject("isDeleted", isDeleted);

		mav.addObject("post", post);
		mav.setViewName("job/detail");
		return mav;
	}



	//  쿠키 유틸 메서드
	/**
	 * 쿠키에서 최근본 공고 ID 목록 읽기
	 * 쿠키값 형식: "1016|1015|1014" (최근 본 순서대로 | 구분)
	 */
	private List<Long> getRecentViewIdsFromCookie(HttpServletRequest request) {
		List<Long> jobIds = new ArrayList<>();

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (RECENT_VIEW_COOKIE.equals(cookie.getName())) {
					String value = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
					if (value != null && !value.isEmpty()) {
						String[] ids = value.split("\\|");
						for (String idStr : ids) {
							try {
								jobIds.add(Long.parseLong(idStr.trim()));
							} catch (NumberFormatException e) {
								// 잘못된 값은 무시
							}
						}
					}
					break;
				}
			}
		}

		return jobIds;
	}

	/**
	 * 쿠키에 최근본 공고 ID 저장
	 * - 이미 있으면 맨 앞으로 이동 (중복 제거)
	 * - 최대 10건 유지
	 * - 수명 7일
	 */
	private void saveRecentViewToCookie(HttpServletRequest request, HttpServletResponse response, Long jobId) {
		// 기존 쿠키에서 ID 목록 읽기
		List<Long> jobIds = getRecentViewIdsFromCookie(request);

		// 중복 제거 (이미 있으면 삭제 후 맨 앞에 추가)
		jobIds.remove(jobId);
		jobIds.add(0, jobId);

		// 최대 건수 초과 시 뒤에서 제거
		if (jobIds.size() > RECENT_VIEW_MAX) {
			jobIds = new ArrayList<>(jobIds.subList(0, RECENT_VIEW_MAX));
		}

		// "1016|1015|1014" 형태로 쿠키값 생성
		String cookieValue = jobIds.stream()
				.map(String::valueOf)
				.collect(Collectors.joining("|"));

		cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8);

		// 쿠키 생성 및 저장
		Cookie cookie = new Cookie(RECENT_VIEW_COOKIE, cookieValue);
		cookie.setMaxAge(COOKIE_MAX_AGE);  // 7일
		cookie.setPath("/");               // 모든 경로에서 접근 가능
		response.addCookie(cookie);
	}
}
