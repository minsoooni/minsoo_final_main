package com.spring.app.company.domain;

import lombok.Data;

@Data
public class PaymentCompleteResponse {
	public boolean ok;
    public String message;

    public String orderId;
    public Long paidAmount;
    public Long pointBalance;
}
