package com.spring.app.company.payment;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PortOneV1Client_imple implements PortOneV1Client {

	@Value("${portone.apiKey}")
    private String apiKey;

    @Value("${portone.apiSecret}")
    private String apiSecret;
	
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    
    @Override
    public String getAccessToken() {
        try {
            String url = "https://api.iamport.kr/users/getToken";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                    "imp_key", apiKey,
                    "imp_secret", apiSecret
            );

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(url, entity, Map.class);

            Map respBody = resp.getBody();
            if (respBody == null) return null;

            Map response = (Map) respBody.get("response");
            if (response == null) return null;

            Object token = response.get("access_token");
            return token == null ? null : String.valueOf(token);

        } catch (Exception e) {
        	System.out.println("[PortOne] getAccessToken 실패");
        	e.printStackTrace();
            return null;
        }
    }

    @Override
    public PortOnePaymentInfo getPaymentInfo(String accessToken, String impUid) {
        try {
            String url = "https://api.iamport.kr/payments/" + impUid;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            Map body = resp.getBody();
            if (body == null) return null;

            Map response = (Map) body.get("response");
            if (response == null) return null;

            PortOnePaymentInfo info = new PortOnePaymentInfo();
            info.status = toStr(response.get("status"));
            info.merchantUid = toStr(response.get("merchant_uid"));
            info.amount = toLong(response.get("amount"));
            return info;

        } catch (Exception e) {
        	System.out.println("[PortOne] getPaymentInfo 실패 - impUid={"+impUid+"}, token={"+accessToken+"}");
        	e.printStackTrace();
            return null;
        }
    }

    
    
    @Override
    public PortOnePaymentInfo getPaymentInfoByMerchantUid(String token, String merchantUid) {
        try {
            RestTemplate rt = new RestTemplate();

            String url = "https://api.iamport.kr/payments/find/" + merchantUid; // merchant_uid 조회
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> resp = rt.exchange(url, HttpMethod.GET, entity, Map.class);

            Map body = resp.getBody();
            if (body == null) return null;

            Map response = (Map) body.get("response");
            if (response == null) return null;

            PortOnePaymentInfo info = new PortOnePaymentInfo();
            info.status = String.valueOf(response.get("status"));
            info.merchantUid = String.valueOf(response.get("merchant_uid"));
            //info.impUid = String.valueOf(response.get("imp_uid"));   // 여기서 '진짜 imp_uid' 확보 가능
            info.setImpUid(String.valueOf(response.get("imp_uid"))); // imp_uid setter로 저장
            info.amount = toLong(response.get("amount"));
            
            //추가: 결제수단 / PG / 간편결제 구분
            info.payMethod = safeStr(response.get("pay_method"));           // card, trans ...
            info.pgProvider = safeStr(response.get("pg_provider"));         // html5_inicis ...
            info.embPgProvider = safeStr(response.get("emb_pg_provider"));  // naverpay, kakaopay ...
            
            return info;
        } catch (Exception e) {
        	 e.printStackTrace(); // 지금은 원인 보려고 일단 찍어두는 걸 추천
            return null;
        }
    }
    
    private String safeStr(Object v) {
        if (v == null) return null;
        String s = String.valueOf(v);
        return "null".equalsIgnoreCase(s) ? null : s;
    }
    
    
    
    
    private String toStr(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        return Long.valueOf(String.valueOf(o));
    }

}
