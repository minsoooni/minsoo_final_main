package com.spring.app.sms.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.spring.app.sms.config.SolapiProperties;
import com.spring.app.sms.util.SolapiAuthUtil;

@Service
public class SolapiSmsService {

    private final SolapiProperties props;
    private final WebClient solapiWebClient;

    public SolapiSmsService(SolapiProperties props, WebClient solapiWebClient) {
        this.props = props;
        this.solapiWebClient = solapiWebClient;
    }

    /**
     * 단문(SMS) 발송 - SOLAPI REST API 직접 호출
     */
    public String sendSms(String toPhone, String text) {

        if (!props.isEnabled()) {
            System.out.println("[SOLAPI DISABLED] to=" + toPhone + ", text=" + text);
            return "DISABLED";
        }

        Instant now = Instant.now();
        String dateIso = java.time.format.DateTimeFormatter.ISO_INSTANT.format(now);

        String authorization = SolapiAuthUtil.createAuthorization(
                props.getApiKey(),
                props.getApiSecret(),
                dateIso
        );

        // SOLAPI v4 send는 보통 messages 배열 형태를 받는 구조가 많음.
        // (필드명/구조는 계정/문서 예시에 따라 달라질 수 있어, 401/400 나오면 다음 스텝에서 바로 맞춤)
        Map<String, Object> requestBody = Map.of(
        	    "message", Map.of(
        	        "to", toPhone,
        	        "from", props.getFromPhone(),
        	        "text", text
        	    )
        	);

        return solapiWebClient.post()
                .uri("/messages/v4/send")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header("x-solapi-date", dateIso)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(errBody -> {
                                    System.out.println("[SOLAPI ERROR BODY] " + errBody);
                                    return new IllegalStateException("SOLAPI ERROR: " + errBody);
                                })
                )
                .bodyToMono(String.class)
                .block();
    }

    public String sendVerificationCode(String toPhone, String code) {
        String text = "[잡파인더] 인증코드: " + code + " (5분 내 입력)";
        return sendSms(toPhone, text);
    }

    public String sendTempPassword(String toPhone, String tempPwd) {
        String text = "[잡파인더] 임시비밀번호: " + tempPwd + " 로그인 후 비밀번호를 변경하세요.";
        return sendSms(toPhone, text);
    }

    public String sendText(String toPhone, String text) {
        return sendSms(toPhone, text);
    }
}