package com.spring.app.jobseeker.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.service.JobPostingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "JobPosting API", description = "채용공고 관련 API")
@RestController
@RequestMapping("/api/job")
public class JobPostingApiController {

    // === 중복 상수화 === //
    private static final String KEY_JOB_ID = "jobId";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MESSAGE = "message";

    private final JobPostingService jobPostingService;

    private static final String RECENT_VIEW_COOKIE = "recentViewedJobs";

    public JobPostingApiController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }


    @Operation(summary = "최근본 공고 목록 조회",
               description = "로그인 시 DB에서, 비로그인 시 쿠키에서 최근본 공고 목록을 조회한다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/recent-viewed")
    public ResponseEntity<List<Map<String, Object>>> getRecentViewed(
            Authentication authentication,
            HttpServletRequest request) {

        List<JobPostingListDTO> recentList;

        if (authentication != null && authentication.isAuthenticated()) {
            // 로그인: DB 조회
            recentList = jobPostingService.getRecentViewedJobs(authentication.getName());
        } else {
            // 비로그인: 쿠키 조회
            List<Long> cookieJobIds = getRecentViewIdsFromCookie(request);
            recentList = cookieJobIds.isEmpty()
                    ? new ArrayList<>()
                    : jobPostingService.getJobPostingsByIds(cookieJobIds);
        }

        // JSON 반환용 Map 변환
        List<Map<String, Object>> result = new ArrayList<>();
        for (JobPostingListDTO dto : recentList) {
            Map<String, Object> map = new HashMap<>();
            map.put(KEY_JOB_ID, dto.getJobId());
            map.put("title", dto.getTitle());
            map.put("companyName", dto.getCompanyName());
            map.put("dDay", dto.getDDay());
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }


    /**
     * 쿠키에서 최근본 공고 ID 목록 읽기
     * 쿠키값 형식: "1016|1015|1014"
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

    // === 공고 신고 API === //
    @PostMapping("/report")
    public Map<String, Object> reportJobPosting(@RequestBody Map<String, Object> params,
                                                 Authentication authentication) {
        Map<String, Object> result = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "로그인이 필요합니다.");
            return result;
        }

        String memberId = authentication.getName();
        Long jobId = Long.valueOf(params.get(KEY_JOB_ID).toString());
        Long reasonId = Long.valueOf(params.get("reasonId").toString());
        String reportContent = (String) params.get("reportContent");

        // 중복 신고 확인
        if (jobPostingService.hasAlreadyReported(jobId, memberId)) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "이미 신고한 공고입니다.");
            return result;
        }

        try {
            jobPostingService.submitReport(jobId, memberId, reasonId, reportContent);
            result.put(KEY_SUCCESS, true);
            result.put(KEY_MESSAGE, "신고가 접수되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "신고 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        return result;
    }
    // === 스크랩 토글 === //
    @PostMapping("/scrap")
    public Map<String, Object> toggleScrap(@RequestBody Map<String, Object> params,
                                            Authentication authentication) {
        Map<String, Object> result = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "로그인이 필요합니다.");
            return result;
        }

        String memberId = authentication.getName();
        Long jobId = Long.valueOf(params.get(KEY_JOB_ID).toString());
        boolean scrap = Boolean.parseBoolean(params.get("scrap").toString());

        try {
            jobPostingService.toggleScrap(jobId, memberId, scrap);
            result.put(KEY_SUCCESS, true);
            result.put("scrapped", scrap);
            result.put(KEY_MESSAGE, scrap ? "스크랩 되었습니다." : "스크랩이 해제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "처리 중 오류가 발생했습니다.");
        }

        return result;
    }
}
