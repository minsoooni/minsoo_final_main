package com.spring.app.company.payment;


//포트원 결제 조회 결과를 저장하는 DTO 
public interface PortOneV1Client {
	String getAccessToken();

    PortOnePaymentInfo getPaymentInfo(String accessToken, String impUid);
    PortOnePaymentInfo getPaymentInfoByMerchantUid(String token, String merchantUid);

    class PortOnePaymentInfo {
        public String status;      // paid, failed, ready 등
        public String merchantUid; // merchant_uid
        public Long amount;        // amount
        
        String impUid;   //추가
        
        // 추가 (결제 수단 관련)
        public String payMethod;     // pay_method (card, trans 등)
        public String pgProvider;    // pg_provider (html5_inicis 등)
        public String embPgProvider; // emb_pg_provider (kakaopay, naverpay 등)
    }
}
