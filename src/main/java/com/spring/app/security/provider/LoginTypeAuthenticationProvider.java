package com.spring.app.security.provider;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.spring.app.member.domain.MemberDTO;
import com.spring.app.member.mapper.MemberMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Component
public class LoginTypeAuthenticationProvider implements AuthenticationProvider {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginTypeAuthenticationProvider(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String memberId = (String) authentication.getPrincipal();
        String rawPassword = (String) authentication.getCredentials();

        // request에서 직접 loginType 꺼내기
        String loginType = "";
        var requestAttributes =
            (org.springframework.web.context.request.ServletRequestAttributes)
            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            loginType = requestAttributes.getRequest().getParameter("loginType");
        }

        // loginType
        // company => 1, member => 0
        Integer expectedType = null;
        if ("company".equalsIgnoreCase(loginType)) expectedType = 1;
        else if ("member".equalsIgnoreCase(loginType)) expectedType = 0;

        if (expectedType == null) {
            throw new BadCredentialsException("로그인 유형이 올바르지 않습니다.");
        }

        // DB 조회 memberId로 조회 (타입 조건은 여기서 직접 체크할거 단일 조회 깔끔)
        MemberDTO db = memberMapper.selectByMemberId(memberId);
        if (db == null) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 타입 체크
        if (db.getType() == null || !db.getType().equals(expectedType)) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // status 분기 (0=정지, 1=활성, 2=탈퇴, 3=휴면)
        Integer status = db.getStatus();
        if (status == null) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (status == 0) {
            throw new org.springframework.security.authentication.DisabledException("MEMBER_SUSPENDED");
        }
        if (status == 2) {
            throw new org.springframework.security.authentication.DisabledException("MEMBER_WITHDRAWN");
        }
        if (status == 3) {
            throw new org.springframework.security.authentication.DisabledException("MEMBER_DORMANT");
        }
        if (status != 1) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 비밀번호 체크 (먼저)
        if (!passwordEncoder.matches(rawPassword, db.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 기업 로그인일 경우 승인 여부 체크 (비번 맞은 뒤에)
        if (expectedType == 1) {  // 1 = 기업
            Integer approvedYn = memberMapper.selectCompanyApprovedYn(memberId);

            if (approvedYn == null || approvedYn == 0) {
                throw new org.springframework.security.authentication.DisabledException("NOT_APPROVED");
            }
            if (approvedYn == 2) {
                throw new org.springframework.security.authentication.DisabledException("REJECT_APPROVED");
            }
        }

        // 권한 조회 후 Security 권한 목록 생성
        List<String> roles = memberMapper.selectAuthorities(memberId);
        var authorities = roles.stream()
                // DB에 ROLE_ 접두사가 없는 경우를 가정해서 보정넣음.(필요없긴함.)
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .toList();

        // 인증 성공 토큰 반환 (권한 포함)
        return new UsernamePasswordAuthenticationToken(db.getMemberId(), null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}