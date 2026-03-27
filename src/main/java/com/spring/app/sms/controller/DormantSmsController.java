package com.spring.app.sms.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.app.member.mapper.MemberMapper;
import com.spring.app.sms.service.SolapiSmsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DormantSmsController {

    private final MemberMapper memberMapper;
    private final SolapiSmsService solapiSmsService;

    public DormantSmsController(MemberMapper memberMapper, SolapiSmsService solapiSmsService) {
        this.memberMapper = memberMapper;
        this.solapiSmsService = solapiSmsService;
    }

    @PostMapping("/member/dormant/send-code")
    public ResponseEntity<?> sendDormantCode(@RequestParam("memberId") String memberId,
                                             HttpSession session) {

        String phone = memberMapper.selectPhoneByMemberId(memberId);
        if (phone == null || phone.isBlank()) {
            return ResponseEntity.badRequest().body("등록된 휴대폰 번호가 없습니다.");
        }

        int codeInt = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String code = String.valueOf(codeInt);

        long expireAt = Instant.now().plusSeconds(5L * 60).toEpochMilli();
        session.setAttribute("DORMANT_SMS_MEMBERID", memberId);
        session.setAttribute("DORMANT_SMS_CODE", code);
        session.setAttribute("DORMANT_SMS_EXPIRE_AT", expireAt);

        String text = "[잡파인더] 휴면 해제 인증번호는 " + code + " 입니다. (5분 이내 입력)";

        String result = solapiSmsService.sendSms(phone, text);

        // 서비스가 DISABLED를 리턴하면 "SENT"가 아니라 실패로 내려야 프론트도 정상 판단 가능
        if ("DISABLED".equalsIgnoreCase(result)) {
            return ResponseEntity.status(503).body("SMS 발송이 비활성화(enabled=false) 상태입니다.");
        }

        // SOLAPI가 정상 응답을 준 경우에만 SENT
        return ResponseEntity.ok("SENT");
    }
    
    @PostMapping("/member/dormant/verify-code")
    public ResponseEntity<?> verifyDormantCode(@RequestParam("memberId") String memberId,
                                               @RequestParam("code") String code,
                                               HttpSession session) {

        String savedMemberId = (String) session.getAttribute("DORMANT_SMS_MEMBERID");
        String savedCode = (String) session.getAttribute("DORMANT_SMS_CODE");
        Long expireAt = (Long) session.getAttribute("DORMANT_SMS_EXPIRE_AT");

        if (savedMemberId == null || savedCode == null || expireAt == null) {
            return ResponseEntity.badRequest().body("인증 정보가 없습니다.");
        }

        // memberId 불일치
        if (!savedMemberId.equals(memberId)) {
            return ResponseEntity.badRequest().body("잘못된 인증 요청입니다.");
        }

        // 시간 만료
        if (System.currentTimeMillis() > expireAt) {
            return ResponseEntity.badRequest().body("인증코드가 만료되었습니다.");
        }

        // 코드 불일치
        if (!savedCode.equals(code)) {
            return ResponseEntity.badRequest().body("인증코드가 올바르지 않습니다.");
        }

        // 인증 성공 DB 변경은 아직 하지 말고, "인증 완료"만 세션에 표시
        session.setAttribute("DORMANT_VERIFIED_MEMBER", memberId);

        // 세션 제거
        session.removeAttribute("DORMANT_SMS_MEMBERID");
        session.removeAttribute("DORMANT_SMS_CODE");
        session.removeAttribute("DORMANT_SMS_EXPIRE_AT");

        return ResponseEntity.ok("SUCCESS");
    }
    
    
    
}