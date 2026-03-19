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

import com.spring.app.jobseeker.service.ScrapService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Scrap API", description = "구직자 스크랩 관련 API (스크랩 취소)")
@Validated
@RestController
@RequestMapping("/api/scrap")
public class ScrapApiController {

    private final ScrapService scrapService;

    public ScrapApiController(ScrapService scrapService) {
        this.scrapService = scrapService;
    }

   
    // 스크랩 취소
    // POST /api/scrap/{jobId}/delete
    @Operation(
        summary = "스크랩 취소",
        description = "로그인한 구직자가 스크랩한 공고를 스크랩 목록에서 제거한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "스크랩 취소 성공/실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "취소 성공",
                        value = """
                                {
                                  "success": true
                                }
                                """
                    ),
                    @ExampleObject(
                        name = "취소 실패 (이미 해제됨 등)",
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
    public ResponseEntity<Map<String, Object>> removeScrap(
            @Parameter(description = "스크랩 취소할 공고 ID", required = true, example = "1001")
            @PathVariable("jobId") Long jobId,
            Principal principal) {

        String memberId = principal.getName();

        int n = scrapService.removeScrap(memberId, jobId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return ResponseEntity.ok(result);
    }
}
