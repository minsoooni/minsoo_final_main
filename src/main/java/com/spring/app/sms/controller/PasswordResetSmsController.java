package com.spring.app.sms.controller;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.app.member.domain.CompanyMemberDTO;
import com.spring.app.member.domain.MemberDTO;
import com.spring.app.member.mapper.MemberMapper;
import com.spring.app.sms.service.SolapiSmsService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PasswordResetSmsController {

    private final MemberMapper memberMapper;
    private final SolapiSmsService solapiSmsService;

    public PasswordResetSmsController(MemberMapper memberMapper,
                                      SolapiSmsService solapiSmsService) {
        this.memberMapper = memberMapper;
        this.solapiSmsService = solapiSmsService;
    }

    /**
     * 비밀번호 재설정용 인증코드 발송
     * - 개인회원: memberId + name + phone 검증 후 발송
     * - 기업회원: memberId + bizNo + phone 검증 후 발송
     */
    @PostMapping("/member/password/send-code")
    public ResponseEntity<?> sendPasswordResetCode(@RequestParam("memberType") String memberType,
                                                   @RequestParam("memberId") String memberId,
                                                   @RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "bizNo", required = false) String bizNo,
                                                   @RequestParam("phone") String phone,
                                                   HttpSession session) {

        String normalizedMemberType = (memberType == null ? "" : memberType.trim());
        String normalizedMemberId = (memberId == null ? "" : memberId.trim());
        String normalizedName = (name == null ? "" : name.trim());
        String normalizedBizNo = (bizNo == null ? "" : bizNo.trim().replaceAll("-", ""));
        String normalizedPhone = (phone == null ? "" : phone.trim());

        if (normalizedMemberId.isBlank() || normalizedPhone.isBlank()) {
            return ResponseEntity.badRequest().body("필수 정보가 누락되었습니다.");
        }

        String verifiedMemberId = null;

        // ========================= 개인회원 검증 =========================
        if ("member".equals(normalizedMemberType)) {

            if (normalizedName.isBlank()) {
                return ResponseEntity.badRequest().body("이름을 입력해주세요.");
            }

            MemberDTO dto = new MemberDTO();
            dto.setMemberId(normalizedMemberId);
            dto.setName(normalizedName);
            dto.setPhone(normalizedPhone);

            MemberDTO member = memberMapper.findMemberForPassword(dto);

            if (member == null || member.getMemberId() == null || member.getMemberId().isBlank()) {
                return ResponseEntity.badRequest().body("입력한 회원 정보가 일치하지 않습니다.");
            }

            verifiedMemberId = member.getMemberId();
        }

        // ========================= 기업회원 검증 =========================
        else if ("company".equals(normalizedMemberType)) {

            if (normalizedBizNo.isBlank()) {
                return ResponseEntity.badRequest().body("사업자등록번호를 입력해주세요.");
            }

            CompanyMemberDTO dto = new CompanyMemberDTO();
            dto.setMemberId(normalizedMemberId);
            dto.setBizNo(normalizedBizNo);
            dto.setPhone(normalizedPhone);

            CompanyMemberDTO company = memberMapper.findCompanyForPassword(dto);

            if (company == null || company.getMemberId() == null || company.getMemberId().isBlank()) {
                return ResponseEntity.badRequest().body("입력한 회원 정보가 일치하지 않습니다.");
            }

            verifiedMemberId = company.getMemberId();
        }

        else {
            return ResponseEntity.badRequest().body("잘못된 회원 유형입니다.");
        }

        // ========================= DB 휴대폰 확인 =========================
        String dbPhone = memberMapper.selectPhoneByMemberId(verifiedMemberId);

        if (dbPhone == null || dbPhone.isBlank()) {
            return ResponseEntity.badRequest().body("등록된 휴대폰 번호가 없습니다.");
        }

        // ========================= 인증코드 생성 =========================
        int codeInt = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String code = String.valueOf(codeInt);

        long expireAt = Instant.now().plusSeconds(5L * 60).toEpochMilli();

        session.setAttribute("PASSWORD_RESET_SMS_MEMBERID", verifiedMemberId);
        session.setAttribute("PASSWORD_RESET_SMS_CODE", code);
        session.setAttribute("PASSWORD_RESET_SMS_EXPIRE_AT", expireAt);

        // 기존 인증 완료 세션이 남아 있으면 제거
        session.removeAttribute("PASSWORD_RESET_VERIFIED_MEMBER");

        // ========================= SMS 발송 =========================
        String text = "[잡파인더] 비밀번호 재설정 인증번호는 " + code + " 입니다. (5분 이내 입력)";
        String result = solapiSmsService.sendSms(dbPhone, text);

        if ("DISABLED".equalsIgnoreCase(result)) {
            return ResponseEntity.status(503).body("SMS 발송이 비활성화(enabled=false) 상태입니다.");
        }

        return ResponseEntity.ok("SENT");
    }

    /**
     * 비밀번호 재설정용 인증코드 검증
     */
    @PostMapping("/member/password/verify-code")
    public ResponseEntity<?> verifyPasswordResetCode(@RequestParam("memberId") String memberId,
                                                     @RequestParam("code") String code,
                                                     HttpSession session) {

        String normalizedMemberId = (memberId == null ? "" : memberId.trim());
        String normalizedCode = (code == null ? "" : code.trim());

        String savedMemberId = (String) session.getAttribute("PASSWORD_RESET_SMS_MEMBERID");
        String savedCode = (String) session.getAttribute("PASSWORD_RESET_SMS_CODE");
        Long expireAt = (Long) session.getAttribute("PASSWORD_RESET_SMS_EXPIRE_AT");

        if (savedMemberId == null || savedCode == null || expireAt == null) {
            return ResponseEntity.badRequest().body("인증 정보가 없습니다.");
        }

        if (!savedMemberId.equals(normalizedMemberId)) {
            return ResponseEntity.badRequest().body("잘못된 인증 요청입니다.");
        }

        if (System.currentTimeMillis() > expireAt) {
            return ResponseEntity.badRequest().body("인증코드가 만료되었습니다.");
        }

        if (!savedCode.equals(normalizedCode)) {
            return ResponseEntity.badRequest().body("인증코드가 올바르지 않습니다.");
        }

        // 인증 성공
        session.setAttribute("PASSWORD_RESET_VERIFIED_MEMBER", normalizedMemberId);

        // 사용 완료된 인증코드 세션 제거
        session.removeAttribute("PASSWORD_RESET_SMS_MEMBERID");
        session.removeAttribute("PASSWORD_RESET_SMS_CODE");
        session.removeAttribute("PASSWORD_RESET_SMS_EXPIRE_AT");

        return ResponseEntity.ok("SUCCESS");
    }
}