package com.spring.app.member.controller;

import com.spring.app.member.dto.DuplicationCheckResponse;
import com.spring.app.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 해당 파일은 MemberController 중 json데이터를 사용하는 메서드만 분리 
// Swagger사용 
@Tag(name = "Member API", description = "회원 관련 API (중복 체크 등)")
@Validated
@RestController
@RequestMapping("/api/members")
public class MemberApiController {

	String ctxPath = "http://localhost:8000/user-service/";

	
    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(
        summary = "아이디 중복 체크",
        description = "memberId가 사용 가능한지 여부를 반환한다. "
                    + "HTTP 200으로 응답하며, 중복 여부는 available 필드로 판단한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공(사용 가능/불가능 모두 200)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DuplicationCheckResponse.class),
                examples = {
                    @ExampleObject(
                        name = "사용 가능",
                        value = """
                                {
                                  "field": "memberId",
                                  "value": "user01",
                                  "available": true
                                }
                                """
                    ),
                    @ExampleObject(
                        name = "중복(사용 불가)",
                        value = """
                                {
                                  "field": "memberId",
                                  "value": "user01",
                                  "available": false
                                }
                                """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "요청 파라미터 오류(누락/빈값 등)",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "memberId 누락/빈값",
                    value = """
                            {
                              "error": "Bad Request",
                              "message": "checkMemberId.memberId: must not be blank"
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/check-memberid")
    public ResponseEntity<DuplicationCheckResponse> checkMemberId(
        @Parameter(description = "검사할 아이디", required = true, example = "user01")
        @RequestParam("memberId") @NotBlank String memberId
    ) {
        boolean duplicated = memberService.isDuplicatedMemberId(memberId);

        DuplicationCheckResponse res = new DuplicationCheckResponse(
            "memberId",
            memberId,
            !duplicated
        );

        return ResponseEntity.ok(res);
    }

    @Operation(
        summary = "이메일 중복 체크",
        description = "email이 사용 가능한지 여부를 반환한다. "
                    + "HTTP 200으로 응답하며, 중복 여부는 available 필드로 판단한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공(사용 가능/불가능 모두 200)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DuplicationCheckResponse.class),
                examples = {
                    @ExampleObject(
                        name = "사용 가능",
                        value = """
                                {
                                  "field": "email",
                                  "value": "test@example.com",
                                  "available": true
                                }
                                """
                    ),
                    @ExampleObject(
                        name = "중복(사용 불가)",
                        value = """
                                {
                                  "field": "email",
                                  "value": "test@example.com",
                                  "available": false
                                }
                                """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "요청 파라미터 오류(누락/빈값/형식 오류)",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "email 빈값",
                        value = """
                                {
                                  "error": "Bad Request",
                                  "message": "checkEmail.email: must not be blank"
                                }
                                """
                    ),
                    @ExampleObject(
                        name = "email 형식 오류",
                        value = """
                                {
                                  "error": "Bad Request",
                                  "message": "checkEmail.email: must be a well-formed email address"
                                }
                                """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    
    
    @GetMapping("/check-email")
    public ResponseEntity<DuplicationCheckResponse> checkEmail(
        @Parameter(description = "검사할 이메일", required = true, example = "test@example.com")
        @RequestParam("email") @NotBlank @Email String email
    ) {
        boolean duplicated = memberService.isDuplicatedEmail(email);

        DuplicationCheckResponse res = new DuplicationCheckResponse(
            "email",
            email,
            !duplicated
        );

        return ResponseEntity.ok(res);
    }
    
    
    // 사업자등록번호 중복 체크
    @GetMapping("/check-bizno")
    public ResponseEntity<Map<String, Object>> checkBizNo(@RequestParam("bizNo") String bizNo) {
        boolean duplicated = memberService.isDuplicatedBizNo(bizNo);
        return ResponseEntity.ok(Map.of("field","bizNo","value",bizNo,"available", !duplicated));
    }
    
    
}