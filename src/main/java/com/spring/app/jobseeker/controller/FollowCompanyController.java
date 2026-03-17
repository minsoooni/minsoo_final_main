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

import com.spring.app.jobseeker.domain.CompanyInfoDTO;
import com.spring.app.jobseeker.service.FollowCompanyService;

@Controller
@RequestMapping("/jobseeker")
public class FollowCompanyController {

    private final FollowCompanyService followCompanyService;

    public FollowCompanyController(FollowCompanyService followCompanyService) {
        this.followCompanyService = followCompanyService;
    }

   
    // 관심 기업 목록 페이지
    // GET /jobseeker/company/followcompanylist
    @GetMapping("company/followcompanylist")
    public ModelAndView followCompanyList(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page",    required = false, defaultValue = "1") int page,
            ModelAndView mav,
            Principal principal) {

        String memberId = principal.getName();

        // === 페이징 계산 === //
        int sizePerPage = 12;
        int startRow = (page - 1) * sizePerPage + 1;
        int endRow = page * sizePerPage;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("keyword",  keyword);
        paraMap.put("startRow", startRow);
        paraMap.put("endRow",   endRow);

        // 총 건수 조회
        int totalCount = followCompanyService.getFollowedCompanyCount(paraMap);

        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPages == 0) totalPages = 1;

        List<CompanyInfoDTO> companies = followCompanyService.getFollowedCompanyList(paraMap);

        mav.addObject("companies",   companies);
        mav.addObject("totalCount",  totalCount);
        mav.addObject("totalPages",  totalPages);
        mav.addObject("currentPage", page);
        mav.addObject("keyword", keyword);
        mav.addObject("activeMenu", "company");

        mav.setViewName("jobseeker/company/followcompanylist");
        return mav;
    }
}
