package com.spring.app.company.payment;


//포트원 결제 조회 결과를 저장하는 DTO
public interface PortOneV1Client {
 String getAccessToken();

 PortOnePaymentInfo getPaymentInfo(String accessToken, String impUid);
 PortOnePaymentInfo getPaymentInfoByMerchantUid(String token, String merchantUid);

 class PortOnePaymentInfo {
     public String status;         // 결제 상태
     public String merchantUid;    // 주문번호
     public Long amount;           // 결제 금액

     private String impUid;        // 포트원 결제 고유번호

     public String getImpUid() {
         return impUid;
     }

     public void setImpUid(String impUid) {
         this.impUid = impUid;
     }

     public String payMethod;      // 결제 수단
     public String pgProvider;     // PG사
     public String embPgProvider;  // 간편결제사
 }
}
