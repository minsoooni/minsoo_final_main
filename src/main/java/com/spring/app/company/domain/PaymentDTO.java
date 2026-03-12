package com.spring.app.company.domain;

import lombok.Data;

@Data
//결제 정보 조회하는 DTO
public class PaymentDTO {
	private Long paymentId;        // 결제ID
    private String memberId;       // 회원ID
    private String orderId;        // 주문ID
    private Long chargeAmount;     // 충전 요청 금액
    private String status;         // 상태 (REQUESTED/PAID/CANCELED 등)
    private String payMethod;
    private String pgProvider;
    private String embPgProvider;

    private String requestedAt;      // 결제요청일시
    private String paidAt;           // 결제완료일시
    private String canceledAt;       // 취소일시

    private String createdAt;        // 등록일시
    private String updatedAt;        // 수정일시
}
