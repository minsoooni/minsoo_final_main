package com.spring.app.sms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.sms.service.SolapiSmsService;

@RestController
public class SmsTestController {

    private final SolapiSmsService smsService;

    public SmsTestController(SolapiSmsService smsService) {
        this.smsService = smsService;
    }

    /**
     * 테스트 문자 발송
     * 예:
     * http://localhost:9080/api/sms/test?phone=01012345678
     */
    @GetMapping("/api/sms/test")
    public String sendTestSms(@RequestParam("phone") String phone) {

        String text = "[잡파인더] SMS 발송 테스트입니다.";

        String result = smsService.sendText(phone, text);

        return "SOLAPI RESPONSE = " + result;
    }
}