package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.jobseeker.domain.CompanyInfoDTO;
import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.service.CompanyInfoService;

/**
 * 기업 정보 웹 컨트롤러 (비로그인/구직자/기업 모두 접근 가능)
 * - 화면(View) 반환만 담당
 * - API는 CompanyInfoApiController에서 처리
 */
@Controller
@RequestMapping("/companyinfo")
public class CompanyInfoController {

    private final CompanyInfoService companyInfoService;

    public CompanyInfoController(CompanyInfoService companyInfoService) {
        this.companyInfoService = companyInfoService;
    }


    // ===== 기업 목록 =====
    // GET /companyinfo/list
    @GetMapping("/list")
    public ModelAndView companyList(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "industry", defaultValue = "") String industry,
            @RequestParam(value = "companyType", defaultValue = "") String companyType,
            @RequestParam(value = "location", defaultValue = "") String location,
            @RequestParam(value = "locationDetail", defaultValue = "") String locationDetail,
            @RequestParam(value = "sort", defaultValue = "follow") String sort,
            @RequestParam(value = "hiringOnly", required = false) String hiringOnly,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            ModelAndView mav) {

        int sizePerPage = 12; // 카드 4열 x 3줄

        // 페이징 계산
        int startRno = (currentPage - 1) * sizePerPage + 1;
        int endRno = startRno + sizePerPage - 1;

        // location, locationDetail을 List로 변환 (콤마 구분)
        List<String> locationList = new ArrayList<>();
        if (location != null && !location.isEmpty()) {
            for (String s : location.split(",")) {
                if (!s.trim().isEmpty()) locationList.add(s.trim());
            }
        }
        List<String> locationDetailList = new ArrayList<>();
        if (locationDetail != null && !locationDetail.isEmpty()) {
            for (String s : locationDetail.split(",")) {
                if (!s.trim().isEmpty()) locationDetailList.add(s.trim());
            }
        }

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("keyword", keyword);
        paraMap.put("industry", industry);
        paraMap.put("companyType", companyType);
        paraMap.put("locationList", locationList);
        paraMap.put("locationDetailList", locationDetailList);
        paraMap.put("sort", sort);
        paraMap.put("hiringOnly", hiringOnly);
        paraMap.put("startRno", startRno);
        paraMap.put("endRno", endRno);

        // 기업 목록 조회
        List<CompanyInfoDTO> companies = companyInfoService.getCompanyList(paraMap);

        // 총 건수 조회
        int totalCount = companyInfoService.getCompanyListTotalCount(paraMap);

        // 페이지 계산
        int totalPages = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPages == 0) totalPages = 1;

        // 페이지 블록 (1~5, 6~10, ...)
        int blockSize = 5;
        int blockNo = (int) Math.ceil((double) currentPage / blockSize);
        int blockStart = (blockNo - 1) * blockSize + 1;
        int blockEnd = Math.min(blockStart + blockSize - 1, totalPages);

        mav.addObject("companies", companies);
        mav.addObject("totalCount", totalCount);
        mav.addObject("keyword", keyword);
        mav.addObject("industry", industry);
        mav.addObject("companyType", companyType);
        mav.addObject("location", location);
        mav.addObject("locationDetail", locationDetail);
        mav.addObject("sort", sort);
        mav.addObject("hiringOnly", hiringOnly);
        mav.addObject("currentPage", currentPage);
        mav.addObject("totalPages", totalPages);
        mav.addObject("blockStart", blockStart);
        mav.addObject("blockEnd", blockEnd);

        mav.setViewName("companyinfo/list");
        return mav;
    }


    // ===== 기업 상세 =====
    // GET /companyinfo/detail/{id}
    @GetMapping("/detail/{id}")
    public ModelAndView companyDetail(@PathVariable("id") String id,
                                     Principal principal,
                                     ModelAndView mav) {

        // 로그인 사용자 ID (비로그인 시 null)
        String loginMemberId = (principal != null) ? principal.getName() : null;

        // 기업 상세 조회
        CompanyInfoDTO company = companyInfoService.getCompanyDetail(id, loginMemberId);

        if (company == null) {
            // 존재하지 않는 기업
            mav.setViewName("redirect:/companyinfo/list");
            return mav;
        }

        // 유사 기업 목록
        List<CompanyInfoDTO> similarCompanies =
                companyInfoService.getSimilarCompanies(id, company.getIndustryCode());

        mav.addObject("company", company);
        mav.addObject("similarCompanies", similarCompanies);

        mav.setViewName("companyinfo/detail");
        return mav;
    }


    // ===== 진행중 채용공고 페이징 (AJAX) =====
    // GET /companyinfo/api/jobpostings?memberId=xxx&page=1
    @GetMapping("/api/jobpostings")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getJobPostingsPaged(
            @RequestParam("memberId") String memberId,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        int sizePerPage = 5;
        int startRow = (page - 1) * sizePerPage + 1;
        int endRow = page * sizePerPage;

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("memberId", memberId);
        paraMap.put("startRow", startRow);
        paraMap.put("endRow", endRow);

        List<JobPostingListDTO> posts = companyInfoService.getCompanyJobPostingsPaged(paraMap);
        int totalCount = companyInfoService.getCompanyJobPostingsCount(memberId);
        int totalPages = (int) Math.ceil((double) totalCount / sizePerPage);
        if (totalPages == 0) totalPages = 1;

        Map<String, Object> result = new HashMap<>();
        result.put("posts", posts);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);

        return ResponseEntity.ok(result);
    }
}
