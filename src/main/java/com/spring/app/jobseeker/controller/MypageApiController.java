package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.jobseeker.domain.ResumeDTO;
import com.spring.app.jobseeker.service.MypageService;
import com.spring.app.member.domain.MemberDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Mypage API", description = "구직자 마이페이지 관련 API (프로필 조회/수정)")
@Validated
@RestController
@RequestMapping("/api/mypage")
public class MypageApiController {

    // === SonarQube: 중복 리터럴 상수화 === //
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_CLASS_NAME = "className";

    private final MypageService mypageService;

    public MypageApiController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

  
    // 프로필 조회
    // GET /api/mypage/profile
    @Operation(
        summary = "프로필 조회",
        description = "로그인한 회원의 프로필 정보(회원정보 + 대표이력서 연동정보)를 반환한다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "프로필 조회 성공",
                    value = """
                            {
                              "member": {
                                "memberId": "user01",
                                "name": "홍길동",
                                "birthDate": "1995-03-15",
                                "email": "hong@example.com",
                                "phone": "010-1234-5678",
                                "gender": 1
                              },
                              "resume": {
                                "photoPath": "photo/user01.jpg",
                                "regionName": "서울",
                                "categoryName": "백엔드 개발"
                              }
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(Principal principal) {

        String memberId = principal.getName();

        MemberDTO member = mypageService.getMemberInfo(memberId);
        ResumeDTO resume = mypageService.getPrimaryResume(memberId);

        Map<String, Object> result = new HashMap<>();
        result.put("member", member);
        result.put("resume", resume);

        return ResponseEntity.ok(result);
    }


    // 프로필 수정 API
    // POST /api/mypage/profile
    @Operation(
        summary = "프로필 수정",
        description = "로그인한 회원의 프로필 정보(이름, 생년월일, 성별, 이메일, 전화번호)를 수정한다. "
                    + "사진, 희망근무지역, 희망직무는 대표이력서에서 수정해야 하므로 변경 불가."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "수정 성공",
                    value = """
                            {
                              "success": true,
                              "message": "프로필이 수정되었습니다."
                            }
                            """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "요청 파라미터 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "필수값 누락",
                    value = """
                            {
                              "success": false,
                              "message": "이름은 필수 입력값입니다."
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody Map<String, String> paramMap,
            Principal principal) {

        Map<String, Object> result = new HashMap<>();

        // === 필수값 검증 === //
        String name = paramMap.get("name");
        String birthDateStr = paramMap.get("birthDate");
        String genderStr = paramMap.get("gender");
        String email = paramMap.get("email");
        String phone = paramMap.get("phone");

        if (name == null || name.trim().isEmpty()) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "이름은 필수 입력값입니다.");
            return ResponseEntity.badRequest().body(result);
        }
        if (birthDateStr == null || birthDateStr.trim().isEmpty()) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "생년월일은 필수 입력값입니다.");
            return ResponseEntity.badRequest().body(result);
        }
        if (genderStr == null || genderStr.trim().isEmpty()) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "성별은 필수 입력값입니다.");
            return ResponseEntity.badRequest().body(result);
        }
        if (email == null || email.trim().isEmpty()) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "이메일은 필수 입력값입니다.");
            return ResponseEntity.badRequest().body(result);
        }
        if (phone == null || phone.trim().isEmpty()) {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "전화번호는 필수 입력값입니다.");
            return ResponseEntity.badRequest().body(result);
        }

        // === DTO 세팅 === //
        String memberId = principal.getName();

        MemberDTO dto = new MemberDTO();
        dto.setMemberId(memberId);
        dto.setName(name.trim());
        dto.setBirthDate(LocalDate.parse(birthDateStr));
        dto.setGender(Integer.parseInt(genderStr));
        dto.setEmail(email.trim());
        dto.setPhone(phone.trim());

        // === DB 수정 === //
        int n = mypageService.updateProfile(dto);

        if (n > 0) {
            result.put(KEY_SUCCESS, true);
            result.put(KEY_MESSAGE, "프로필이 수정되었습니다.");
            return ResponseEntity.ok(result);
        } else {
            result.put(KEY_SUCCESS, false);
            result.put(KEY_MESSAGE, "프로필 수정에 실패했습니다.");
            return ResponseEntity.ok(result);
        }
    }


    // 캘린더 이벤트 조회
    @Operation(summary = "캘린더 이벤트 조회", description = "지원일/마감일/제안 응답기한을 FullCalendar 형식으로 반환한다.")
    @GetMapping("/calendar/events")
    public ResponseEntity<List<Map<String, Object>>> getCalendarEvents(Principal principal) {
        String memberId = principal.getName();
        List<Map<String, Object>> rawEvents = mypageService.getCalendarEvents(memberId);

        List<Map<String, Object>> fcEvents = new java.util.ArrayList<>();
        for (Map<String, Object> row : rawEvents) {
            Map<String, Object> ev = new HashMap<>();
            ev.put("title", row.get("title"));
            ev.put("fullTitle", row.get("fullTitle"));
            ev.put("start", row.get("date"));
            ev.put("type", row.get("type"));
            ev.put("company", row.get("company"));
            ev.put("jobId", row.get("jobId"));
            ev.put("offerSubmitId", row.get("offerSubmitId"));

            String type = (String) row.get("type");
            if ("applied".equals(type))       ev.put(KEY_CLASS_NAME, "ev-applied");
            else if ("deadline".equals(type))  ev.put(KEY_CLASS_NAME, "ev-deadline");
            else if ("offer".equals(type))     ev.put(KEY_CLASS_NAME, "ev-offer");
            else                               ev.put(KEY_CLASS_NAME, "ev-applied");

            fcEvents.add(ev);
        }
        return ResponseEntity.ok(fcEvents);
    }
}
