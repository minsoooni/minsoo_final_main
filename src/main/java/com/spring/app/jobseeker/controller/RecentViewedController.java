package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.jobseeker.service.RecentViewedService;

@Controller
@RequestMapping("/jobseeker")
public class RecentViewedController {

    private final RecentViewedService recentViewedService;

    public RecentViewedController(RecentViewedService recentViewedService) {
        this.recentViewedService = recentViewedService;
    }

   
    // 최근 본 공고 목록 페이지
    // GET /jobseeker/job/recent
    @GetMapping("job/recent")
    public ModelAndView recentViewedList(
            @RequestParam(value = "keyword",  required = false, defaultValue = "") String keyword,
            @RequestParam(value = "dateFrom", required = false, defaultValue = "") String dateFrom,
            @RequestParam(value = "dateTo",   required = false, defaultValue = "") String dateTo,
            @RequestParam(value = "period",   required = false, defaultValue = "") String period,
            @RequestParam(value = "page",     required = false, defaultValue = "1") int page,
            ModelAndView mav,
            Principal principal) {

        String memberId = principal.getName();

        // === 페이징 계산 === //
        int sizePerPage = 10;
        int startRow = (page - 1) * sizePerPage + 1;
        int endRow = page * sizePerPage;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("keyword",  keyword);
        paraMap.put("dateFrom", dateFrom);
        paraMap.put("dateTo",   dateTo);
        paraMap.put("period",   period);
        paraMap.put("startRow", startRow);
        paraMap.put("endRow",   endRow);

        // 총 건수 조회
        int totalCount = recentViewedService.getRecentViewedPostCount(paraMap);

        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPages == 0) totalPages = 1;

        // 목록 조회
        List<Map<String, Object>> recentPosts = recentViewedService.getRecentViewedPostList(paraMap);

        // === 날짜별 그룹핑 === //
        // viewedDate(YYYY.MM.DD) 기준으로 그룹 생성
        Map<String, List<Map<String, Object>>> groupMap = new LinkedHashMap<>();
        for (Map<String, Object> post : recentPosts) {
            String viewedDate = (String) post.get("viewedDate");
            if (viewedDate == null) viewedDate = "날짜 없음";
            groupMap.computeIfAbsent(viewedDate, k -> new ArrayList<>()).add(post);
        }

        // dateGroups 리스트 변환 (Thymeleaf에서 사용)
        List<Map<String, Object>> dateGroups = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupMap.entrySet()) {
            Map<String, Object> group = new HashMap<>();
            group.put("date",  entry.getKey());
            group.put("posts", entry.getValue());
            dateGroups.add(group);
        }

        // === Model 세팅 === //
        mav.addObject("recentPosts",  recentPosts);
        mav.addObject("dateGroups",   dateGroups);
        mav.addObject("totalCount",   totalCount);
        mav.addObject("totalPages",   totalPages);
        mav.addObject("currentPage",  page);
        mav.addObject("keyword",      keyword);
        mav.addObject("dateFrom",     dateFrom);
        mav.addObject("dateTo",       dateTo);
        mav.addObject("period",       period);
        mav.addObject("activeMenu",   "recent");

        mav.setViewName("jobseeker/job/recentViewed");
        return mav;
    }
}
