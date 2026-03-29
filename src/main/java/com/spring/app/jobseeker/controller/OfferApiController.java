package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.jobseeker.service.OfferService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Jobseeker Offer API", description = "구직자 받은 제안 관련 REST API (수락/거절/열람)")
@RestController
@RequestMapping("/api/offer")

public class OfferApiController {

    // === 중복 상수화 === //
    private static final String KEY_RESULT = "result";

    private final OfferService offerService;

    public OfferApiController(OfferService offerService) {
        this.offerService = offerService;
    }


    // 제안 열람 처리
    // POST /api/offer/{offerSubmitId}/view
    @Operation(summary = "제안 열람 처리", description = "미열람 상태의 제안을 열람 처리한다.")
    @PostMapping("/{offerSubmitId}/view")
    public ResponseEntity<Map<String, Object>> markAsViewed(
            @PathVariable("offerSubmitId") long offerSubmitId,
            Principal principal) {

        String memberId = principal.getName();
        int n = offerService.markAsViewed(offerSubmitId, memberId);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_RESULT, n >= 0 ? "ok" : "fail");
        return ResponseEntity.ok(result);
    }


    // 제안 수락
    // POST /api/offer/{offerSubmitId}/accept
    @Operation(summary = "제안 수락", description = "받은 제안을 수락한다.")
    @PostMapping("/{offerSubmitId}/accept")
    public ResponseEntity<Map<String, Object>> acceptOffer(
            @PathVariable("offerSubmitId") long offerSubmitId,
            Principal principal) {

        String memberId = principal.getName();
        int n = offerService.acceptOffer(offerSubmitId, memberId);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_RESULT, n == 1 ? "ok" : "fail");
        if (n != 1) {
            result.put("message", "이미 응답한 제안이거나 존재하지 않는 제안입니다.");
        }
        return ResponseEntity.ok(result);
    }


    // 제안 거절
    // POST /api/offer/{offerSubmitId}/reject
    @Operation(summary = "제안 거절", description = "받은 제안을 거절한다.")
    @PostMapping("/{offerSubmitId}/reject")
    public ResponseEntity<Map<String, Object>> rejectOffer(
            @PathVariable("offerSubmitId") long offerSubmitId,
            Principal principal) {

        String memberId = principal.getName();
        int n = offerService.rejectOffer(offerSubmitId, memberId);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_RESULT, n == 1 ? "ok" : "fail");
        if (n != 1) {
            result.put("message", "이미 응답한 제안이거나 존재하지 않는 제안입니다.");
        }
        return ResponseEntity.ok(result);
    }
}
