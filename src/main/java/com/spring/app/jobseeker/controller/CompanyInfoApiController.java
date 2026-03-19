package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.service.CompanyInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CompanyInfo API", description = "기업정보 관련 API (팔로우 등)")
@RestController
@RequestMapping("/api/companyinfo")
public class CompanyInfoApiController {

    private final CompanyInfoService companyInfoService;

    public CompanyInfoApiController(CompanyInfoService companyInfoService) {
        this.companyInfoService = companyInfoService;
    }


    // 팔로우 토글
    // POST /api/companyinfo/follow
    @Operation(
        summary = "기업 팔로우 토글",
        description = "로그인한 구직자가 기업을 팔로우/언팔로우한다. "
                    + "이미 팔로우 중이면 해제, 아니면 등록."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "팔로우 토글 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "팔로우 성공",
                    value = """
                            {
                              "success": true,
                              "action": "followed",
                              "followerCount": 43
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
    })
    @PostMapping("/follow")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            @RequestParam(value = "companyMemberId") String companyMemberId,
            Principal principal) {

        if (principal == null) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("success", false);
            errorMap.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorMap);
        }

        String loginMemberId = principal.getName();
        Map<String, Object> result = companyInfoService.toggleFollow(companyMemberId, loginMemberId);
        result.put("success", true);

        return ResponseEntity.ok(result);
    }


    // 진행중 채용공고 페이징
    // GET /api/companyinfo/jobpostings?memberId=xxx&page=1
    @Operation(
        summary = "기업 채용공고 페이징 조회",
        description = "기업 상세 페이지에서 진행중인 채용공고를 페이징하여 조회한다."
    )
    @GetMapping("/jobpostings")
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
