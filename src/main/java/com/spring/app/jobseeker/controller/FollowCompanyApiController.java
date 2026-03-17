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

import com.spring.app.jobseeker.service.FollowCompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Follow Company API", description = "구직자 관심기업 관련 API (팔로우 해제)")
@Validated
@RestController
@RequestMapping("/api/follow")
public class FollowCompanyApiController {

    private final FollowCompanyService followCompanyService;

    public FollowCompanyApiController(FollowCompanyService followCompanyService) {
        this.followCompanyService = followCompanyService;
    }

   
    // 팔로우 해제 API
    // POST /api/follow/{companyMemberId}/delete
    @Operation(
        summary = "팔로우 해제",
        description = "로그인한 구직자가 관심기업 목록에서 해당 기업의 팔로우를 해제한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "팔로우 해제 성공/실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "해제 성공",
                        value = """
                                {
                                  "success": true
                                }
                                """
                    ),
                    @ExampleObject(
                        name = "해제 실패 (이미 해제됨 등)",
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
    @PostMapping("/{companyMemberId}/delete")
    public ResponseEntity<Map<String, Object>> removeFollow(
            @Parameter(description = "팔로우 해제할 기업 회원 ID", required = true, example = "company01")
            @PathVariable("companyMemberId") String companyMemberId,
            Principal principal) {

        String memberId = principal.getName();

        int n = followCompanyService.removeFollow(memberId, companyMemberId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", n > 0);
        return ResponseEntity.ok(result);
    }
}
