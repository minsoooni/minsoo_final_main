package com.spring.app.company.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.company.domain.BannerDTO;
import com.spring.app.company.domain.BannerListDTO;
import com.spring.app.company.domain.JobPostingDTO;
import com.spring.app.company.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Company - Banner REST API (AJAX/Swagger)
 */
@Tag(name = "Company - Banner API", description = "기업 배너 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company/api/banner")
public class CompanyBannerApiController {
    private final CompanyService service;
    
    

    @Operation(summary = "배너 등록용 공고 목록 조회", description = "로그인한 기업의 공고 목록을 반환한다.")
    @GetMapping("/jobs")
    public ResponseEntity<Map<String, Object>> getMyJobPostingList(Authentication authentication) {

        Map<String, Object> map = new HashMap<>();

        try {
            String memberId = authentication != null ? authentication.getName() : "test_company";

            List<JobPostingDTO> jobList = service.getBannerPostingList(memberId);

            map.put("result", 1);
            map.put("jobList", jobList);

        } catch (Exception e) {
            e.printStackTrace();
            map.put("result", 0);
            map.put("message", "공고 목록 조회 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(map);
    }
    
    
    
    @Operation(summary = "배너 등록", description = "배너 정보와 이미지를 함께 등록한다.")
    @PostMapping
    public ResponseEntity<Map<String, Object>> insertBanner(
            @ModelAttribute BannerDTO bannerDto,
            @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage,
            Authentication authentication) {

        Map<String, Object> map = new HashMap<>();

        try {
            String memberId = authentication != null ? authentication.getName() : "test_company";
            bannerDto.setFkMemberId(memberId);
            
            if (bannerDto.getFkJobId() == null) {
                map.put("result", 0);
                map.put("message", "연결할 공고를 선택해 주세요.");
                return ResponseEntity.ok(map);
            }

            if (bannerDto.getTitle() == null || bannerDto.getTitle().trim().isEmpty()) {
                map.put("result", 0);
                map.put("message", "배너 제목을 입력해 주세요.");
                return ResponseEntity.ok(map);
            }

            if (bannerDto.getStartAt() == null || bannerDto.getStartAt().trim().isEmpty()) {
                map.put("result", 0);
                map.put("message", "시작일을 선택해 주세요.");
                return ResponseEntity.ok(map);
            }

            service.insertBannerWithImage(bannerDto, bannerImage);

            map.put("result", 1);
            map.put("message", "배너가 등록되었습니다.");

        } catch (Exception e) {
            e.printStackTrace();
            map.put("result", 0);
            map.put("message", e.getMessage() != null ? e.getMessage() : "배너 등록 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(map);
    }

    
    
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBannerList(Authentication authentication) {

        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = authentication.getName();

            List<BannerListDTO> bannerList = service.getBannerListByMemberId(memberId);

            result.put("success", true);
            result.put("bannerList", bannerList);
            result.put("count", bannerList.size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "배너 목록 조회 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    
    
    //포인트 정보 조회
    @Operation(summary = "배너 결제 정보 조회", description = "로그인한 기업의 현재 포인트와 배너 광고비를 반환한다.")
    @GetMapping("/payment-info")
    public ResponseEntity<Map<String, Object>> getBannerPaymentInfo(Authentication authentication) {

        Map<String, Object> result = new HashMap<>();

        try {
            String memberId = authentication != null ? authentication.getName() : null;

            Map<String, Object> paymentInfo = service.getBannerPaymentInfo(memberId);

            result.put("result", 1);
            result.put("paymentInfo", paymentInfo);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", 0);
            result.put("message", "배너 결제 정보 조회 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok(result);
    }
    
}
