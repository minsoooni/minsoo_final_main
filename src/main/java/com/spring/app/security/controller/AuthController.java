package com.spring.app.security.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.security.auth.AuthService;
import com.spring.app.security.jwt.JwtToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("memberid") String memberid,
                                   @RequestParam("passwd") String passwd,
                                   @RequestParam("loginType") String loginType,
                                   HttpServletRequest request) {
        try {
            JwtToken jwtToken = authService.login(memberid, passwd, loginType);

            HttpSession session = request.getSession();
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", jwtToken.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtToken.getAccessTokenExpiresIn() / 1000)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", jwtToken.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60L * 60 * 24 * 14)
                    .sameSite("Lax")
                    .build();

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("grantType", jwtToken.getGrantType());
            body.put("accessToken", jwtToken.getAccessToken());
            body.put("accessTokenExpiresIn", jwtToken.getAccessTokenExpiresIn());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(body);

        } catch (DisabledException e) {
            if ("MUST_CHANGE_PASSWORD".equals(e.getMessage())) {
                HttpSession session = request.getSession();
                session.setAttribute("PASSWORD_RESET_VERIFIED_MEMBER", memberid);
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        } catch (BadCredentialsException e) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        } catch (LockedException e) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.LOCKED).body(body);
        }
    }
    
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request) {

        try {
            String refreshToken = null;

            if (request.getCookies() != null) {
                for (var cookie : request.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken == null || refreshToken.isBlank()) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", "REFRESH_TOKEN_MISSING");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            JwtToken jwtToken = authService.reissue(refreshToken);

            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", jwtToken.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtToken.getAccessTokenExpiresIn() / 1000)
                    .sameSite("Lax")
                    .build();

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", jwtToken.getRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60L * 60 * 24 * 14)
                    .sameSite("Lax")
                    .build();

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("grantType", jwtToken.getGrantType());
            body.put("accessToken", jwtToken.getAccessToken());
            body.put("accessTokenExpiresIn", jwtToken.getAccessTokenExpiresIn());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(body);

        } catch (Exception e) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
    }
    
    
    @GetMapping("/check")
    public ResponseEntity<?> check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", authenticated);

        if (!authenticated) {
            body.put("error", "UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }

        body.put("memberId", authentication.getName());
        return ResponseEntity.ok(body);
    }

    
    
}