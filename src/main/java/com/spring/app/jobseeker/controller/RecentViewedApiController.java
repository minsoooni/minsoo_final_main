package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.jobseeker.service.RecentViewedService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Recent Viewed API", description = "구직자 최근본공고 관련 API (개별 삭제, 전체 삭제)")
@Validated
@RestController
@RequestMapping("/api/recent")
public class RecentViewedApiController {

    private final RecentViewedService recentViewedService;

    public RecentViewedApiController(RecentViewedService recentViewedService) {
        this.recentViewedService = recentViewedService;
    }

    
    // 최근본 공고 개별 삭제
    // POST /api/recent/{jobId}/delete
    @Operation(
        summary = "최근본 공고 개별 삭제",
        description = "로그인한 구직자의 최근본 공고 목록에서 특정 공고를 삭제한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "삭제 성공/실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "삭제 성공",
                        value = """
                                {
                                  "success": true
                                }
                                """
                    ),
                    @ExampleObject(
                        name = "삭제 실패 (이미 삭제됨 등)",
                        value = """
                                {
                                  "success": false
                                }
                                """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{jobId}/delete")
    public ResponseEntity<Map<String, Object>> removeViewedPost(
            @Parameter(description = "삭제할 공고 ID", required = true, example = "1001")
            @PathVariable("jobId") Long jobId,
            Principal principal) {

        String memberId = principal.getName();

        int n = recentViewedService.removeViewedPost(memberId, jobId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return ResponseEntity.ok(result);
    }

   
    // 최근본 공고 전체 삭제
    // POST /api/recent/clear
    @Operation(
        summary = "최근본 공고 전체 삭제",
        description = "로그인한 구직자의 최근본 공고 기록을 모두 삭제한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "전체 삭제 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "전체 삭제 성공",
                    value = """
                            {
                              "success": true
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearAllViewedPosts(Principal principal) {

        String memberId = principal.getName();

        recentViewedService.removeAllViewedPosts(memberId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}
