package com.spring.app.jobseeker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.common.FileManager;
import com.spring.app.jobseeker.domain.*;
import com.spring.app.jobseeker.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@Tag(name = "이력서 API", description = "구직자 이력서 CRUD 및 부가 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resume")
public class ResumeApiController {

    // === 중복 상수화 === //
    private static final String KEY_RESULT = "result";
    private static final String KEY_MESSAGE = "message";

    private final ResumeService resumeService;
    private final FileManager fileManager;

    @Value("${file.images-dir}")
    private String uploadPath;

  
    // 이력서 저장
    @Operation(summary = "이력서 저장", description = "이력서 기본 정보 + 하위 항목(학력,경력,어학,자격증,포트폴리오,수상,기술스택)을 한번에 저장한다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "저장 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "저장 성공",
                    value = """
                            {
                              "result": 1,
                              "resumeId": 10
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody ResumeDTO dto,
                                                     Authentication authentication) {
        dto.setMemberid(((String) authentication.getPrincipal()));

        // 이력서 5개 제한 체크
        int count = resumeService.selectResumeCountByMember(((String) authentication.getPrincipal()));
        if (count >= 5) {
            Map<String, Object> map = new HashMap<>();
            map.put(KEY_RESULT, 0);
            map.put(KEY_MESSAGE, "이력서는 최대 5개까지 작성할 수 있습니다.");
            return ResponseEntity.ok(map);
        }

        int result = resumeService.insertResume(dto);

        Map<String, Object> map = new HashMap<>();
        map.put(KEY_RESULT, result);
        map.put("resumeId", dto.getResumeId());
        return ResponseEntity.ok(map);
    }

 
    // 이력서 수정  
    @Operation(summary = "이력서 수정", description = "이력서 기본 정보 수정 + 하위 항목 전체 삭제 후 재등록")
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
                              "result": 1
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody ResumeDTO dto,
                                                       Authentication authentication) {
        dto.setMemberid(((String) authentication.getPrincipal()));
        int result = resumeService.updateResume(dto);
        return ResponseEntity.ok(Map.of(KEY_RESULT, result, "resumeId", dto.getResumeId()));
    }

  
    // 이력서 삭제
    @Operation(summary = "이력서 삭제", description = "이력서를 소프트 삭제한다")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "삭제 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "삭제 성공",
                    value = """
                            {
                              "result": 1
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/delete/{resumeId}")
    public ResponseEntity<Map<String, Object>> delete(
            @Parameter(description = "이력서 ID") @PathVariable("resumeId") long resumeId,
            Authentication authentication) {
        int result = resumeService.deleteResume(resumeId, ((String) authentication.getPrincipal()));
        return ResponseEntity.ok(Map.of(KEY_RESULT, result));
    }

    
    // 이력서 상세 조회
    @Operation(summary = "이력서 상세 조회", description = "이력서 기본 정보 + 하위 항목 전체를 조회한다.")
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
                              "resumeId": 1,
                              "memberid": "user01",
                              "categoryId": 10,
                              "regionCode": "R01",
                              "title": "주력 이력서",
                              "address": "서울시 강남구",
                              "selfIntro": "Spring Boot 개발자입니다.",
                              "desiredSalary": 5000,
                              "photoPath": "photo/user01.jpg",
                              "writeStatus": 1,
                              "isPrimary": 1,
                              "allowScout": 1,
                              "memberName": "홍길동",
                              "memberEmail": "hong@example.com",
                              "memberPhone": "010-1234-5678",
                              "categoryName": "백엔드 개발",
                              "regionName": "서울",
                              "educationList": [],
                              "careerList": [],
                              "languageList": [],
                              "certificateList": [],
                              "portfolioList": [],
                              "awardList": [],
                              "techstackList": []
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{resumeId}")
    public ResponseEntity<Map<String, Object>> detail(
            @Parameter(description = "이력서 ID") @PathVariable("resumeId") long resumeId) {
        ResumeDTO resume = resumeService.selectResumeOne(resumeId);

        Map<String, Object> map = new HashMap<>();
        map.put("resume", resume);
        return ResponseEntity.ok(map);
    }

 
    // 이력서 목록 조회
    @Operation(summary = "이력서 목록 조회", description = "로그인한 회원의 이력서 목록을 조회한다.")
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
                              "list": [
                                {
                                  "resumeId": 1,
                                  "title": "주력 이력서",
                                  "categoryName": "백엔드 개발",
                                  "writeStatus": 1,
                                  "isPrimary": 1
                                },
                                {
                                  "resumeId": 2,
                                  "title": "신입 지원용",
                                  "categoryName": "풀스택",
                                  "writeStatus": 1,
                                  "isPrimary": 0
                                }
                              ]
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(Authentication authentication) {
        List<ResumeDTO> list = resumeService.selectResumeListByMember(((String) authentication.getPrincipal()));
        return ResponseEntity.ok(Map.of("list", list));
    }


    // 대표 이력서 설정
    @Operation(summary = "대표 이력서 설정", description = "기존 대표이력서를 해제하고 해당 이력서를 대표이력서로 설정한다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "설정 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "설정 성공",
                    value = """
                            {
                              "result": 1
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/primary/{resumeId}")
    public ResponseEntity<Map<String, Object>> setPrimary(
            @Parameter(description = "이력서 ID") @PathVariable("resumeId") long resumeId,
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        String memberid = (String) authentication.getPrincipal();
        int allowScout = ((Number) body.get("allowScout")).intValue();
        int result = resumeService.setPrimaryResume(resumeId, memberid, allowScout);
        return ResponseEntity.ok(Map.of(KEY_RESULT, result));
    }

    
    // 제안 허용 설정
    @PutMapping("/scout/{resumeId}")
    public ResponseEntity<Map<String, Object>> toggleScout(
            @PathVariable("resumeId") long resumeId,
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        int allowScout = ((Number) body.get("allowScout")).intValue();
        String memberid = (String) authentication.getPrincipal();
        int result = resumeService.updateAllowScout(resumeId, memberid, allowScout);
        return ResponseEntity.ok(Map.of(KEY_RESULT, result));
    }

  
    // 자격증 검색 (자동완성)
    @Operation(summary = "자격증 검색 (자동완성)", description = "자격증 마스터 테이블에서 키워드로 자격증을 검색한다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "검색 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "검색 성공",
                    value = """
                            {
                              "list": [
                                {
                                  "certificateCode": "C001",
                                  "certificateName": "정보처리기사",
                                  "issuer": "한국산업인력공단"
                                }
                              ]
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/certificate/search")
    public ResponseEntity<List<CertificateDTO>> searchCertificate(
            @Parameter(description = "검색 키워드") @RequestParam(value = "keyword") String keyword) {
        List<CertificateDTO> list = resumeService.searchCertificateMaster(keyword);
        return ResponseEntity.ok(list);
    }

 
    // 기술스택 검색 (자동완성)
    @Operation(summary = "기술스택 검색", description = "키워드로 기술스택을 검색한다 (자동완성용)")
    @GetMapping("/skill/search")
    public ResponseEntity<List<ResumeTechstackDTO>> searchSkill(
            @Parameter(description = "검색 키워드") @RequestParam(value = "keyword") String keyword) {
        List<ResumeTechstackDTO> list = resumeService.searchSkill(keyword);
        return ResponseEntity.ok(list);
    }


    // 직무 카테고리 목록
    @Operation(summary = "직무 카테고리 목록", description = "이력서 작성 시 선택할 수 있는 직무 카테고리 목록을 조회한다.")
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
                              "list": [
                                {
                                  "categoryId": 1,
                                  "parentId": null,
                                  "categoryName": "개발",
                                  "depth": 1
                                },
                                {
                                  "categoryId": 10,
                                  "parentId": 1,
                                  "categoryName": "백엔드 개발",
                                  "depth": 2
                                }
                              ]
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<JobCategoryDTO> list = resumeService.selectJobCategoryList();
        return ResponseEntity.ok(Map.of("list", list));
    }

    // 지역 목록
    @Operation(summary = "지역 목록", description = "이력서 작성 시 선택할 수 있는 지역 목록을 조회한다.")
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
                              "list": [
                                {
                                  "regionCode": "R01",
                                  "parentRegionCode": null,
                                  "regionName": "서울",
                                  "regionLevel": 1
                                },
                                {
                                  "regionCode": "R01-01",
                                  "parentRegionCode": "R01",
                                  "regionName": "강남구",
                                  "regionLevel": 2
                                }
                              ]
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/regions")
    public ResponseEntity<Map<String, Object>> getRegions() {
        List<RegionDTO> list = resumeService.selectRegionList();
        return ResponseEntity.ok(Map.of("list", list));
    }


    // 프로필 사진 업로드
    @Operation(summary = "프로필 사진 업로드", description = "이력서 프로필 사진을 업로드하고 저장된 경로를 반환한다.")
    @PostMapping("/uploadPhoto")
    public ResponseEntity<Map<String, Object>> uploadPhoto(
            @RequestParam("photo") MultipartFile multipartFile,
            Authentication authentication) {

        Map<String, Object> map = new HashMap<>();

        if (multipartFile.isEmpty()) {
            map.put(KEY_RESULT, 0);
            map.put(KEY_MESSAGE, "파일이 없습니다.");
            return ResponseEntity.ok(map);
        }

        try {
            byte[] bytes = multipartFile.getBytes();
            String originalFilename = multipartFile.getOriginalFilename();
            String newFileName = fileManager.doFileUpload(bytes, originalFilename, uploadPath + "/jobseeker");

            // DB에 저장할 경로
            String photoPath = "/images/jobseeker/" + newFileName;

            map.put(KEY_RESULT, 1);
            map.put("photoPath", photoPath);

        } catch (Exception e) {
            e.printStackTrace();
            map.put(KEY_RESULT, 0);
            map.put(KEY_MESSAGE, "업로드 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(map);
    }

    
    // 포트폴리오 파일 업로드   
    @Operation(summary = "포트폴리오 파일 업로드", description = "포트폴리오 파일을 업로드하고 저장된 경로를 반환한다.")
    @PostMapping("/uploadPortfolio")
    public ResponseEntity<Map<String, Object>> uploadPortfolio(
            @RequestParam("file") MultipartFile multipartFile,
            Authentication authentication) {

        Map<String, Object> map = new HashMap<>();

        if (multipartFile.isEmpty()) {
            map.put(KEY_RESULT, 0);
            map.put(KEY_MESSAGE, "파일이 없습니다.");
            return ResponseEntity.ok(map);
        }

        try {
            byte[] bytes = multipartFile.getBytes();
            String originalFilename = multipartFile.getOriginalFilename();
            String newFileName = fileManager.doFileUpload(bytes, originalFilename, uploadPath + "/portfolio");

            String filepath = "/images/portfolio/" + newFileName;

            map.put(KEY_RESULT, 1);
            map.put("filepath", filepath);
            map.put("fileName", originalFilename);

        } catch (Exception e) {
            e.printStackTrace();
            map.put(KEY_RESULT, 0);
            map.put(KEY_MESSAGE, "업로드 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(map);
    }
}
