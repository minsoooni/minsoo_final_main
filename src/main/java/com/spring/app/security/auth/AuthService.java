package com.spring.app.security.auth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.spring.app.member.domain.MemberDTO;
import com.spring.app.member.mapper.MemberMapper;
import com.spring.app.security.jwt.JwtToken;
import com.spring.app.security.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberMapper memberMapper;
	private final HttpSession session;

	public AuthService(AuthenticationManager authenticationManager,
	                   JwtTokenProvider jwtTokenProvider,
	                   MemberMapper memberMapper,
	                   HttpSession session) {
	    this.authenticationManager = authenticationManager;
	    this.jwtTokenProvider = jwtTokenProvider;
	    this.memberMapper = memberMapper;
	    this.session = session;
	}

    public JwtToken login(String memberId, String password, String loginType) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberId, password);

        var authentication = authenticateWithFailHandling(authenticationToken, memberId);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String authenticatedMemberId = authentication.getName();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dormantCutoff = now.minusMonths(6);

        MemberDTO meta = memberMapper.selectLoginMeta(authenticatedMemberId);

        if (meta == null) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        boolean alreadyDormant = (meta.getStatus() != null && meta.getStatus() == 3);
        boolean overSixMonths = (meta.getLastLoginAt() != null && meta.getLastLoginAt().isBefore(dormantCutoff));

        if (alreadyDormant || overSixMonths) {
            if (!alreadyDormant) {
                memberMapper.markDormant(authenticatedMemberId, now);
            }
            throw new DisabledException("MEMBER_DORMANT");
        }

        memberMapper.updateLastLoginAt(authenticatedMemberId, now);
        memberMapper.resetFail(authenticatedMemberId);

        // 권한
        var authorities = authentication.getAuthorities();

        // access token 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                authentication.getName(),
                null,
                authorities
        );

        if (meta.getMustChangePasswordYn() != null && meta.getMustChangePasswordYn() == 1) {
            throw new DisabledException("MUST_CHANGE_PASSWORD");
        }
        
        // refresh token 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(
                authentication.getName()
        );

        // refresh token DB 저장
        memberMapper.upsertRefreshToken(authenticatedMemberId, refreshToken);

        
        
        
        
        // JWT 응답 생성
        long accessTokenExpiresIn = 60000L;

        JwtToken jwtToken = JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .refreshToken(refreshToken)
                .build();

        session.setAttribute("accessToken", accessToken);

        String expireAt = LocalDateTime.now()
                .plusSeconds(accessTokenExpiresIn / 1000)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("==================================================");
        System.out.println("[JWT LOGIN SUCCESS]");
        System.out.println("memberId       : " + authenticatedMemberId);
        System.out.println("accessToken    : " + accessToken);
        System.out.println("refreshToken   : " + refreshToken);
        System.out.println("accessExpireAt : " + expireAt);
        System.out.println("==================================================");

        return jwtToken;
        
    }
    
    
    
    
    
    
    
    private Authentication authenticateWithFailHandling(
            UsernamePasswordAuthenticationToken authenticationToken,
            String memberId) {

        try {
            return authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            if (memberId != null && !memberId.isBlank()) {
                MemberDTO meta = memberMapper.selectLoginMeta(memberId);

                if (meta != null && meta.getStatus() != null && meta.getStatus() == 1) {
                    LocalDateTime now = LocalDateTime.now();

                    memberMapper.upsertIncreaseFail(memberId, now);

                    Integer cnt = memberMapper.selectFailCount(memberId);
                    int failCount = (cnt == null ? 0 : cnt);

                    if (failCount >= 5) {
                        memberMapper.markDormant(memberId, now);
                        throw new DisabledException("MEMBER_DORMANT");
                    }
                }
            }

            throw e;
        }
    }
    
    
    
    
    public JwtToken reissue(String refreshToken) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("REFRESH_TOKEN_INVALID");
        }

        String memberId = jwtTokenProvider.getMemberId(refreshToken);

        String savedRefreshToken = memberMapper.selectRefreshToken(memberId);

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new BadCredentialsException("REFRESH_TOKEN_NOT_MATCH");
        }

        var authorities = memberMapper.selectAuthorities(memberId)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        String newAccessToken = jwtTokenProvider.createAccessToken(
                memberId,
                null,
                authorities
        );

        System.out.println("==================================================");
        System.out.println("[JWT REISSUE SUCCESS]");
        System.out.println("memberId       : " + memberId);
        System.out.println("refreshToken   : " + refreshToken);
        System.out.println("newAccessToken : " + newAccessToken);
        System.out.println("==================================================");

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(60000L)
                .build();
    }
    
    
}