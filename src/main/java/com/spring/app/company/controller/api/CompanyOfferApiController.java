package com.spring.app.company.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.company.domain.OfferCreateRequestDTO;
import com.spring.app.company.domain.OfferDetailDTO;
import com.spring.app.company.domain.OfferRecipientDetailDTO;
import com.spring.app.company.domain.OfferSendRequestDTO;
import com.spring.app.company.domain.OfferUpdateRequestDTO;
import com.spring.app.company.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Company - Offer REST API (AJAX/Swagger)
 */
@Tag(name = "Company - Offer API", description = "기업 제안서(스카웃) 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company/api/offers")
public class CompanyOfferApiController {
    private final CompanyService service;

    
    //제안서 정보 조회
    @Operation(summary = "제안서 지표 조회(상단 합계 + 목록별)", description = "상단 합계(summary)와 제안서별 지표(items)를 함께 반환")
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> offerMetrics(Authentication authentication) {
        String companyMemberId = authentication.getName();

        return ResponseEntity.ok(Map.of(
            "result", "ok",
            "summary", service.selectOfferMetricsSummary(companyMemberId),
            "items", service.selectOfferMetricsByCompany(companyMemberId)
        ));
    }
    
    //제안서 상세 조회
    @Operation(summary = "제안서 상세 조회", description = "클릭한 제안서에 대한 상세 내용을 조회")
    @GetMapping("/{offerLetterId}")
    public ResponseEntity<OfferDetailDTO> offerDetail(@PathVariable("offerLetterId") Long offerLetterId) {
        OfferDetailDTO dto = service.selectOfferDetail(offerLetterId);
        System.out.println(dto);
        /*
        OfferDetailDTO(offerLetterId=23, jobId=1023, title=수정일자 수정 후 테스트제안서 1, message=지원자 전달 멧지, expireAt=2026-03-22T00:00)
        */        
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    //제안서 생성
    @Operation(summary = "제안서 생성", description = "새로운 제안서를 작성")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOffer(@RequestBody OfferCreateRequestDTO req) {
        Long offerLetterId = service.createOfferLetter(req);
        return ResponseEntity.ok(Map.of("result", "ok", "offerLetterId", offerLetterId));
    }

    //제안서 수정
    @Operation(summary = "제안서 수정", description = "클릭한 제안서에 대한 내용을 수정")
    @PutMapping("/{offerLetterId}")
    public ResponseEntity<Map<String, Object>> updateOffer(
            			  @PathVariable("offerLetterId") Long offerLetterId,
            			  @RequestBody OfferUpdateRequestDTO req) {

    	//System.out.println("offerLetterId: " +offerLetterId);
    	//System.out.println("req:" + req);
    	//offerLetterId: 23
    	//req:OfferUpdateRequestDTO(offerLetterId=23, jobId=null, title=수정일자 수정 후 테스트제안서 1, message=테스트111111111)
    	
    	
        req.setOfferLetterId(offerLetterId);
        int n = service.updateOfferLetter(req);
        return ResponseEntity.ok(Map.of("result", n == 1 ? "ok" : "fail"));
    }
    
    
    //제안서 삭제
    @Operation(summary = "제안서 삭제", description = "제안서를 삭제합니다.")
    @DeleteMapping("/{offerLetterId}")
    public ResponseEntity<Map<String, Object>> deleteOffer(
            			  @PathVariable("offerLetterId") long offerLetterId,
            			  Authentication authentication) {
        
    	String companyMemberId = authentication.getName();

        int n = service.deleteOfferLetter(offerLetterId, companyMemberId);

        // n==1이면 삭제 성공, 아니면 실패(권한없음/없음)
        return ResponseEntity.ok(Map.of("result", n == 1 ? "ok" : "fail"));
    }
    
    
    
    
    //제안서 발송
    @Operation(summary = "제안서 발송", description = "체크박스의 체크된 구직자에게 제안서를 발송")
    @PostMapping("/{offerLetterId}/send")
    public ResponseEntity<Map<String, Object>> sendOffer(
            @PathVariable("offerLetterId") Long offerLetterId,
            @RequestBody OfferSendRequestDTO req,
            Authentication authentication) {

        try {
            req.setOfferLetterId(offerLetterId);
            String companyMemberId = authentication.getName();
            Long offerSubmitId = service.sendOffer(req, companyMemberId);

            return ResponseEntity.ok(Map.of(
                    "result", "ok",
                    "offerSubmitId", offerSubmitId
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "result", "fail",
                    "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of(
                    "result", "fail",
                    "message", e.getMessage()
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of(
                    "result", "fail",
                    "message", e.getMessage()
            ));
        }
    }
    
    
    //제안서를 발송한 회원(memberId) 목록 조회
    @Operation(summary = "제안서 발송 수신자 조회", description = "해당 제안서(offerLetterId)를 이미 발송한 수신자(memberId) 목록을 조회")
    @GetMapping("/{offerLetterId}/sent-recipients")
    public ResponseEntity<Map<String, Object>> sentRecipients(
            @PathVariable("offerLetterId") Long offerLetterId,
            Authentication authentication
    ) {
    	String companyMemberId = authentication.getName();
    	
        return ResponseEntity.ok(Map.of(
                "result", "ok",
                "sentMemberIds", service.selectSentMemberIdsByOfferLetterId(offerLetterId, companyMemberId)
        ));
    }
    
    //수신자 상세 조회
    @GetMapping("/{offerLetterId}/recipient-details")
    public ResponseEntity<Map<String, Object>> recipientDetails(
            @PathVariable("offerLetterId") Long offerLetterId,
            Authentication authentication) {

        try {
            String companyMemberId = authentication.getName();

            List<OfferRecipientDetailDTO> list =
                    service.selectOfferRecipientDetailsByOfferLetterId(offerLetterId, companyMemberId);

            return ResponseEntity.ok(Map.of(
                    "result", "ok",
                    "items", list
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "result", "fail",
                    "message", e.getMessage()
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of(
                    "result", "fail",
                    "message", e.getMessage()
            ));
        }
    }
    
    
    
    
    // 삭제된 원본 제안서 중 발송 이력이 있는 목록
    @Operation(summary = "삭제한 제안서에 대한 발송내역 조회", description = "삭제한 제안서의 수신자별 발송/열람/응답 상태를 조회")
    @GetMapping("/deleted-history")
    public ResponseEntity<Map<String, Object>> deletedOfferHistory(Authentication authentication) {
        String companyMemberId = authentication.getName();

        return ResponseEntity.ok(Map.of(
            "result", "ok",
            "items", service.selectDeletedOfferHistoryList(companyMemberId)
        ));
    }
}
