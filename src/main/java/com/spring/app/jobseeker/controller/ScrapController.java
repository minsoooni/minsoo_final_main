package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.jobseeker.service.ScrapService;

@Controller
@RequestMapping("/jobseeker")
public class ScrapController {

    private final ScrapService scrapService;

    public ScrapController(ScrapService scrapService) {
        this.scrapService = scrapService;
    }

   
    // 스크랩한 공고 목록 페이지
    // GET /jobseeker/job/scrapped
    @GetMapping("job/scrapped")
    public ModelAndView scrappedList(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "sort",    required = false, defaultValue = "latest") String sort,
            @RequestParam(value = "page",    required = false, defaultValue = "1") int page,
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
        paraMap.put("sort",     sort);
        paraMap.put("startRow", startRow);
        paraMap.put("endRow",   endRow);

        // 총 건수 조회
        int totalCount = scrapService.getScrappedPostCount(paraMap);

        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPages == 0) totalPages = 1;

        // 스크랩한 공고 목록
        List<Map<String, Object>> scrappedPosts = scrapService.getScrappedPostList(paraMap);

        mav.addObject("scrappedPosts", scrappedPosts);
        mav.addObject("totalCount",  totalCount);
        mav.addObject("totalPages",  totalPages);
        mav.addObject("currentPage", page);
        mav.addObject("keyword", keyword);
        mav.addObject("sort", sort);
        mav.addObject("activeMenu", "scrapped");

        mav.setViewName("jobseeker/job/scrapped");
        return mav;
    }
}
