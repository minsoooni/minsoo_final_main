package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.jobseeker.domain.ImageFileDTO;
import com.spring.app.jobseeker.service.ApplyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Apply API", description = "구직자 지원 관련 API (지원취소, 중복확인, 상태조회)")
@RestController
@RequestMapping("/api/apply")
public class ApplyApiController {

   
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MESSAGE = "message";

    private final ApplyService applyService;

    public ApplyApiController(ApplyService applyService) {
        this.applyService = applyService;
    }


   
    // 1. 지원 취소
    @Operation(
        summary = "지원 취소",
        description = "미열람 상태인 지원을 취소한다. 기업이 이미 열람했거나 취소된 지원은 취소 불가."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "처리 결과",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "취소 성공",
                        value = """
                                {
                                  "success": true,
                                  "message": "지원이 취소되었습니다."
                                }
                                """
                    ),
                    @ExampleObject(
                        name = "취소 실패",
                        value = """
                                {
                                  "success": false,
                                  "message": "지원 취소에 실패했습니다. 이미 열람되었거나 취소된 지원입니다."
                                }
                                """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
    })
    @PostMapping("/cancel/{applicationId}")
    public ResponseEntity<Map<String, Object>> cancelApplication(
            @Parameter(description = "지원 ID") @PathVariable("applicationId") Long applicationId,
            Principal principal) {

        Map<String, Object> result = new HashMap<>();

        if (principal == null) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(result);
        }

        String memberid = principal.getName();

        int n = applyService.cancelApplication(applicationId, memberid);
        if (n == 1) {
            result.put(KEY_SUCCESS, true);
            result.put(KEY_MESSAGE, "지원이 취소되었습니다.");
        } else {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "지원 취소에 실패했습니다. 이미 열람되었거나 취소된 지원입니다.");
        }

        return ResponseEntity.ok(result);
    }


  
    // 2. 지원 여부 확인
    @Operation(
        summary = "지원 여부 확인",
        description = "해당 공고에 이미 지원했는지 확인한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "확인 결과",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "확인 결과",
                    value = """
                            {
                              "applied": true
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
    })
    @GetMapping("/check/{jobId}")
    public ResponseEntity<Map<String, Object>> checkApplied(
            @Parameter(description = "공고 ID") @PathVariable("jobId") Long jobId,
            Principal principal) {

        Map<String, Object> result = new HashMap<>();

        if (principal == null) {
            result.put("applied", false);
            return ResponseEntity.ok(result);
        }

        String memberid = principal.getName();
        boolean applied = applyService.hasAlreadyApplied(jobId, memberid);
        result.put("applied", applied);

        return ResponseEntity.ok(result);
    }


    
    // 3. 지원 상태별 건수 조회
    @Operation(
        summary = "지원 상태별 건수 조회",
        description = "로그인한 회원의 지원 상태별 건수를 조회한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "조회 성공",
                    value = """
                            {
                              "total": 12,
                              "submitted": 4,
                              "reviewing": 3,
                              "interview": 2,
                              "passed": 1,
                              "rejected": 2
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
    })
    @GetMapping("/status-counts")
    public ResponseEntity<Map<String, Integer>> getStatusCounts(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        String memberid = principal.getName();
        Map<String, Integer> counts = applyService.getApplicationStatusCounts(memberid);

        return ResponseEntity.ok(counts);
    }


  
    // 4. 지원서 첨부파일 목록 조회
    @Operation(
        summary = "지원서 첨부파일 목록 조회",
        description = "해당 지원의 첨부파일 목록을 조회한다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "조회 성공",
                value = """
                        {
                          "files": [
                            {
                              "fileId": 1,
                              "fileUrl": "20260228_abc123.pdf",
                              "originalFilename": "포트폴리오.pdf"
                            }
                          ]
                        }
                        """
            )
        )
    )
    @GetMapping("/{applicationId}/files")
    public ResponseEntity<Map<String, Object>> getFiles(
            @Parameter(description = "지원 ID") @PathVariable("applicationId") Long applicationId,
            Principal principal) {

        Map<String, Object> result = new HashMap<>();

        if (principal == null) {
            result.put("files", List.of());
            return ResponseEntity.status(401).body(result);
        }

        List<ImageFileDTO> files = applyService.getApplicationFiles(applicationId);
        result.put("files", files);

        return ResponseEntity.ok(result);
    }
}
