package com.spring.app.member.controller;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.app.member.domain.CompanyMemberDTO;
import com.spring.app.member.domain.MemberDTO;
import com.spring.app.member.domain.MemberRegisterRequest;
import com.spring.app.member.mapper.MemberMapper;
import com.spring.app.member.service.MemberService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MemberController {
	
	private final MemberService memberService;
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	
	public MemberController(MemberService memberService, MemberMapper memberMapper, PasswordEncoder passwordEncoder ) {
	    this.memberService = memberService;
	    this.memberMapper = memberMapper;
	    this.passwordEncoder = passwordEncoder;
	}
	
	// =========================== 공용 ===========================
	
	// 로그인 페이지 이동
	@GetMapping("/member/login")
    public String loginView() {
        return "member/login";
    }
	
	// 회원가입 완료 페이지
	@GetMapping("/member/registerSuccess")
	public String registerSuccess(@RequestParam(value = "type", required = false) String type, Model model) {
	    model.addAttribute("type", type); // member / company
	    return "member/registerSuccess";
	}
	
    // 휴면 계정 안내 페이지 이동
    @GetMapping("/member/dormant")
    public String dormantPage(@RequestParam(value="memberId", required=false) String memberId,
                              Model model) {

        // memberId가 안 넘어오면 로그인 세션에서 꺼내기
        if (memberId == null || memberId.isBlank()) {
            var auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                memberId = auth.getName();
            } else {
                // 로그인도 아닌데 들어오면 로그인 페이지로
                return "redirect:/member/login";
            }
        }

        model.addAttribute("memberId", memberId);
        return "member/dormant";
    }
    
    // 휴면 계정 해제 처리
    @PostMapping("/member/dormant/unlock")
    public String unlockDormant(@RequestParam("memberId") String memberId,
                                HttpSession session) {

        // 인증 완료 여부 체크 (세션에 심어둔 값)
        String verifiedMemberId = (String) session.getAttribute("DORMANT_VERIFIED_MEMBER");

        // 인증이 안됐거나, 다른 memberId로 unlock 시도하면 차단
        if (verifiedMemberId == null || !verifiedMemberId.equals(memberId)) {
            return "redirect:/member/dormant?memberId=" + memberId + "&reason=needSms";
        }

        LocalDateTime now = LocalDateTime.now();

        // 휴면 해제
        memberMapper.unlockDormant(memberId, now);
        
        return "redirect:/member/password/reset";
    }

    // 비밀번호 변경 페이지 이동 (로그인 사용자 / 강제 비밀번호 변경 대상)
    @GetMapping("/member/password/change")
    public String passwordChangePage(Model model) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/member/login";
        }

        String memberId = auth.getName();
        model.addAttribute("memberId", memberId);

        return "member/passwordChange"; // templates/member/passwordChange.html
    }

    // 비밀번호 변경 처리
    @PostMapping("/member/password/change")
    public String passwordChangeEnd(@RequestParam("currentPassword") String currentPassword, @RequestParam("newPassword") String newPassword, 
    		                        @RequestParam("newPasswordConfirm") String newPasswordConfirm, Model model){
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/member/login";
        }

        String memberId = auth.getName();

        // 새 비번/확인 일치
        if (newPassword == null || !newPassword.equals(newPasswordConfirm)) {
            model.addAttribute("memberId", memberId);
            model.addAttribute("error", "newPasswordMismatch");
            return "member/passwordChange";
        }

        // 현재 비번 일치 확인 (DB 해시 vs 입력값 matches)
        MemberDTO db = memberMapper.selectByMemberId(memberId);
        if (db == null) {
            return "redirect:/member/login";
        }

        if (!passwordEncoder.matches(currentPassword, db.getPassword())) {
            model.addAttribute("memberId", memberId);
            model.addAttribute("error", "currentPasswordInvalid");
            return "member/passwordChange";
        }

        // 비번 업데이트 + 강제변경 플래그 0으로 내리기
        String encoded = passwordEncoder.encode(newPassword);
        memberMapper.updatePassword(memberId, encoded);
        memberMapper.updateMustChangePasswordYn(memberId, 0);

        return "redirect:/index?pwChanged=1";
    }
    
    
    // 로그인 없이 접근 가능한 비밀번호 재설정 페이지 이동 -> 휴면 SMS 인증 완료 후 세션에 인증 정보가 있어야 접근 가능
    @GetMapping("/member/password/reset")
    public String passwordResetPage(HttpSession session, Model model) {

        String verifiedMemberId = (String) session.getAttribute("DORMANT_VERIFIED_MEMBER");

        if(verifiedMemberId == null) {
            verifiedMemberId = (String) session.getAttribute("PASSWORD_RESET_VERIFIED_MEMBER");
        }

        if (verifiedMemberId == null || verifiedMemberId.isBlank()) {
            return "redirect:/member/findAccount?findType=password";
        }

        model.addAttribute("memberId", verifiedMemberId);
        return "member/passwordReset";
    }

    // 로그인 없이 비밀번호 재설정 처리
    @PostMapping("/member/password/reset")
    public String passwordResetEnd(@RequestParam("newPassword") String newPassword,
                                   @RequestParam("newPasswordConfirm") String newPasswordConfirm,
                                   HttpSession session, Model model) {

        String verifiedMemberId = (String) session.getAttribute("DORMANT_VERIFIED_MEMBER");

        if(verifiedMemberId == null) {
            verifiedMemberId = (String) session.getAttribute("PASSWORD_RESET_VERIFIED_MEMBER");
        }

        if (verifiedMemberId == null || verifiedMemberId.isBlank()) {
            return "redirect:/member/findAccount?findType=password";
        }

        if (newPassword == null || !newPassword.equals(newPasswordConfirm)) {
            model.addAttribute("memberId", verifiedMemberId);
            model.addAttribute("error", "newPasswordMismatch");
            return "member/passwordReset";
        }

        String encoded = passwordEncoder.encode(newPassword);

        memberMapper.updatePassword(verifiedMemberId, encoded);
        memberMapper.updateMustChangePasswordYn(verifiedMemberId, 0);
        memberMapper.resetFail(verifiedMemberId);

        session.removeAttribute("DORMANT_VERIFIED_MEMBER");
        session.removeAttribute("PASSWORD_RESET_VERIFIED_MEMBER");

        return "redirect:/member/login?pwReset=1";
    }
    
    // 아이디/비밀번호 찾기 페이지 이동
    @GetMapping("/member/findAccount")
    public String findAccountPage() {
        return "member/findAccount";
    }

	// =========================== 개인회원 ===========================
	
	// (구직자) 회원가입 이동 
	@GetMapping("/member/registerMember")
	public String registerMemberView(@ModelAttribute("req") MemberRegisterRequest req) {
	    return "member/registerMember";
	}
	
    // (구직자) 회원가입 동작 메서드
    @PostMapping("/member/registerMemberEnd")
    public String registerMemberEnd(
            @Valid @ModelAttribute("req") MemberRegisterRequest req,
            BindingResult br
    ) {
        // 비밀번호 일치 검증(서버)
        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            br.rejectValue("passwordConfirm", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }

        // 아이디/이메일 중복 검증(서버 단)
        if (!br.hasFieldErrors("memberId")
                && memberService.isDuplicatedMemberId(req.getMemberId())) {
            br.rejectValue("memberId", "duplicate.memberId", "이미 사용 중인 아이디입니다.");
        }
        if (!br.hasFieldErrors("email")
                && memberService.isDuplicatedEmail(req.getEmail())) {
            br.rejectValue("email", "duplicate.email", "이미 사용 중인 이메일입니다.");
        }
        if (br.hasErrors()) {
            return "member/registerMember";
        }
        try {
            memberService.registerPersonal(req);
        } catch (IllegalArgumentException e) {
            // 서비스단 예외를 폼 에러로 변환
            String msg = e.getMessage();

            if (msg != null && msg.contains("아이디")) {
                br.rejectValue("memberId", "duplicate.memberId", msg);
            } else if (msg != null && msg.contains("이메일")) {
                br.rejectValue("email", "duplicate.email", msg);
            } else {
                br.reject("register.failed", msg != null ? msg : "회원가입에 실패했습니다.");
            }
            return "member/registerMember";
        }

        return "redirect:/member/registerSuccess?type=member";
    }

    // 구직자 회원 아이디 찾기 처리
    @PostMapping("/member/find/memberId")
    public String findMemberId(MemberDTO dto, Model model) {

        String foundMemberId = memberService.findMemberId(dto);

        model.addAttribute("memberType", "member");
        model.addAttribute("findType", "id");
        model.addAttribute("resultType", "memberId");

        if (foundMemberId == null || foundMemberId.isBlank()) {
            model.addAttribute("notFound", true);
        } else {
            model.addAttribute("foundMemberId", maskMemberId(foundMemberId));
        }

        return "member/findAccount";
    }

    // 구직자 회원 비밀번호 찾기 처리
    @PostMapping("/member/find/memberPassword")
    public String findMemberPassword(MemberDTO dto, HttpSession session, Model model) {

        MemberDTO member = memberService.findMemberForPassword(dto);

        if(member == null) {
            model.addAttribute("memberType", "member");
            model.addAttribute("findType", "password");
            model.addAttribute("notFound", true);
            return "member/findAccount";
        }

        // 본인확인 성공 → 세션 저장
        session.setAttribute("PASSWORD_RESET_VERIFIED_MEMBER", member.getMemberId());

        // 비밀번호 재설정 페이지 이동
        return "redirect:/member/password/reset";
    }

	// =========================== 기업회원 ===========================
	
	// (회사) 회원가입 페이지 이동
	@GetMapping("/member/registerCompanyMember")
    public String registerCompanyMember() {
        return "member/registerCompanyMember";
    }

	// (회사) 회원가입 동작 메서드
    @PostMapping("/member/registerCompanyMemberEnd")
    public String registerCompanyMemberEnd(@ModelAttribute CompanyMemberDTO dto) {

        memberService.registerCompany(dto);

        return "redirect:/member/registerSuccess?type=company";
    }
 
    // 회사 회원 아이디 찾기 처리
    @PostMapping("/member/find/companyId")
    public String findCompanyId(CompanyMemberDTO dto, Model model) {

        String foundMemberId = memberService.findCompanyId(dto);

        model.addAttribute("memberType", "company");
        model.addAttribute("findType", "id");
        model.addAttribute("resultType", "companyId");

        if (foundMemberId == null || foundMemberId.isBlank()) {
            model.addAttribute("notFound", true);
        } else {
            model.addAttribute("foundMemberId", maskMemberId(foundMemberId));
        }

        return "member/findAccount";
    }

    // 회사 회원 비밀번호 찾기 처리
    @PostMapping("/member/find/companyPassword")
    public String findCompanyPassword(CompanyMemberDTO dto, HttpSession session, Model model) {

        CompanyMemberDTO company = memberService.findCompanyForPassword(dto);

        if(company == null) {
            model.addAttribute("memberType", "company");
            model.addAttribute("findType", "password");
            model.addAttribute("notFound", true);
            return "member/findAccount";
        }

        // 본인확인 성공 → 세션 저장
        session.setAttribute("PASSWORD_RESET_VERIFIED_MEMBER", company.getMemberId());

        // 비밀번호 재설정 페이지 이동
        return "redirect:/member/password/reset";
    }
    
    private String maskMemberId(String memberId) {

        if (memberId == null || memberId.isBlank()) {
            return null;
        }

        int len = memberId.length();

        // 1글자
        if (len == 1) {
            return "*";
        }

        // 2글자
        if (len == 2) {
            return memberId.substring(0, 1) + "*";
        }

        // 3글자
        if (len == 3) {
            return memberId.substring(0, 1) + "*" + memberId.substring(2);
        }

        // 4글자 이상
        int visiblePrefix = 2;
        int visibleSuffix = 1;
        int maskCount = len - (visiblePrefix + visibleSuffix);

        return memberId.substring(0, visiblePrefix)
                + "*".repeat(maskCount)
                + memberId.substring(len - visibleSuffix);
    }
    
}