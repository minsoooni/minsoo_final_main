package com.spring.app.company.domain;

import lombok.Data;

@Data
public class PaymentReadyResponse {
	public boolean ok;
    public String message;

    public String orderId;       // merchant_uid
    public Long chargeAmount;

    public String orderName;     // 예: 포인트 충전
    public String buyerName;
    public String buyerEmail;
    public String buyerTel;
    
    public Long payAmount; // 실제 결제 금액(프로젝트용 100원)
}
