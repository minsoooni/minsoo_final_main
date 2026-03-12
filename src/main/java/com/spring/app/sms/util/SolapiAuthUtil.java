package com.spring.app.sms.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SolapiAuthUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** ISO 8601 UTC 예: 2019-07-01T00:41:48Z */
    public static String nowIsoUtc() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

    /** 16바이트 랜덤을 hex 문자열로 (salt는 "문자열"이어야 함) */
    public static String randomSaltHex() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return HexFormat.of().formatHex(saltBytes);
    }

    /** signature = HMAC_SHA256(key=apiSecret, data=(dateTime + salt)) 결과를 hex로 */
    public static String generateSignature(String apiSecret, String dateTime, String salt) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal((dateTime + salt).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("SOLAPI signature 생성 실패", e);
        }
    }
   
    public static String createAuthorization(String apiKey, String apiSecret, String dateIsoUtc) {
        String salt = randomSaltHex();
        String signature = generateSignature(apiSecret, dateIsoUtc, salt);

        return "HMAC-SHA256 apiKey=" + apiKey
                + ", date=" + dateIsoUtc
                + ", salt=" + salt
                + ", signature=" + signature;
    }
}