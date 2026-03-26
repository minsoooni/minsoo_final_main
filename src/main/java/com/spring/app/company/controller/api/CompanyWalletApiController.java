package com.spring.app.company.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.company.domain.PaymentCompleteRequest;
import com.spring.app.company.domain.PaymentCompleteResponse;
import com.spring.app.company.domain.PaymentReadyRequest;
import com.spring.app.company.domain.PaymentReadyResponse;
import com.spring.app.company.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Company - Wallet REST API (AJAX/Swagger)
 */
@Tag(name = "Company - Wallet API", description = "기업 결제-포인트 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company/Wallet")
public class CompanyWalletApiController {
    private final CompanyService service;
    
    @Value("${portone.impCode}")
    private String impCode;

    
    // (뷰에서 IMP.init용으로 필요하면 제공)
    @Operation(summary = "포트원 가맹점 식별코드 조회", description = "프론트에서 IMP.init에 사용할 impCode를 반환한다.")
    @GetMapping("/portone/impCode")
    public String getImpCode() {
        return impCode;
    }

    // ===== 결제 준비 =====
    @Operation(summary = "포인트 충전 결제 준비", description = "주문번호를 생성하고 결제(PENDING) 레코드를 만든 뒤 결제창 호출에 필요한 정보를 반환한다.")
    @PostMapping("/payments/ready")
    public PaymentReadyResponse ready(@RequestBody PaymentReadyRequest req, Authentication authentication) {
        return service.preparePointCharge(req, authentication);
    }

    // ===== 결제 완료(서버 검증 + 포인트 적립) =====
    @Operation(summary = "포인트 충전 결제 완료 처리", description = "imp_uid로 포트원 결제정보를 조회하여 금액/주문번호를 검증하고, 결제 완료 처리 후 포인트를 적립한다.")
    @PostMapping("/payments/complete")
    public PaymentCompleteResponse complete(@RequestBody PaymentCompleteRequest req) {
        return service.completePointCharge(req);
    }
    
    
    // 결제 재확인(서버 오류 후 복구용)
    @Operation(summary = "포인트 충전 결제 재확인", description = "주문번호 기준으로 포트원 결제를 다시 조회하여 결제 반영 여부를 복구한다.")
    @GetMapping("/payments/reconcile/{orderId}")
    public PaymentCompleteResponse reconcile(@PathVariable String orderId) {
        return service.reconcilePointCharge(orderId);
    }
    
    
    // 결제창 취소 시 대기 주문을 취소 상태로 정리
    @PostMapping("/payments/cancel-pending")
    public PaymentCompleteResponse cancelPendingPayment(@RequestParam("orderId") String orderId) {
        return service.cancelPendingPayment(orderId);
    }

    
}
