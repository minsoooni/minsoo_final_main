package com.spring.app.company.domain;

import lombok.Data;

@Data
//결제 성공 DTO
public class PaymentCompleteRequest {
	public String impUid;
    public String merchantUid;
}
