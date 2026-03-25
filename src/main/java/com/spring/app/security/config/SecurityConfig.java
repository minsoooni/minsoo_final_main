package com.spring.app.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spring.app.member.mapper.MemberMapper;
import com.spring.app.security.filter.DormantAccountFilter;
import com.spring.app.security.jwt.JwtAuthenticationFilter;
import com.spring.app.security.provider.LoginTypeAuthenticationProvider;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpSession;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	private final MemberMapper memberMapper;
	private final DormantAccountFilter dormantAccountFilter;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	
	public SecurityConfig(MemberMapper memberMapper, DormantAccountFilter dormantAccountFilter, JwtAuthenticationFilter jwtAuthenticationFilter) {
	    this.memberMapper = memberMapper;
	    this.dormantAccountFilter = dormantAccountFilter;
	    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(
	        AuthenticationConfiguration configuration) throws Exception {
	    return configuration.getAuthenticationManager();
	}

    //비밀번호 암호화를 위한 PasswordEncoder 빈 등록 - BCrypt는 salt를 포함한 해시 방식으로, Spring Security에서 권장되는 방식이다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 실패(401 성격) 시 처리 인증되지 않은 사용자가 보호된 자원에 접근하려 할 때 실행된다. -> 여기서는 지정한 안내 페이지로 리다이렉트한다.
    @Bean
    AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) ->
                response.sendRedirect(request.getContextPath() + "/security/noAuthenticated");
    }

    /* 권한 실패(403) 시 처리 로그인은 했지만, 해당 리소스 접근 권한이 없을 때 실행된다.-> 여기서는 지정한 안내 페이지로 리다이렉트한다.*/
    @Bean
    AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                response.sendRedirect(request.getContextPath() + "/security/noAuthorized");
    }

    /* 핵심 보안 설정(SecurityFilterChain) -> 어떤 URL을 허용/차단할지, 로그인/로그아웃을 어떻게 처리할지, 예외(401/403)를 어떻게 처리할지 등을 정의한다.*/
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            LoginTypeAuthenticationProvider loginTypeAuthenticationProvider
    ) throws Exception {

        /* CSRF 비활성화 -> Spring Security는 기본적으로 CSRF 보호가 켜져 있어 POST 요청 시 토큰이 필요하다., 실습/초기 개발 단계에서는 편의상 disable 하는 경우가 많다.
         *   (운영/실서비스에서는 상황에 맞게 재검토 필요) */
    	http.csrf(csrf -> csrf.disable());
    	http.authenticationProvider(loginTypeAuthenticationProvider);

        String[] excludeUri = {
    	    "/",
    	    "/index",
    	    "/member/login",
    	    "/member/registerMember",
    	    "/member/registerCompanyMember",
    	    "/member/findAccount/**",
            "/member/password/change",
    	    "/security/noAuthenticated",
    	    "/security/noAuthorized",
    	    "/security/loginEnd",
    	    "/swagger-ui/**",
    	    "/swagger-ui.html",
    	    "/v3/api-docs/**",
    	    "/opendata/**",
    	    "/upload/**",
    	    "/photoupload/**",
    	    "/emailattachfile/**",
    	    "/images/**",
    	    "/job/**",
    	    "/api/job/**",
    	    "/companyinfo/**",
    	    "/auth/login",
    	    "/auth/reissue",
    	    "/auth/check",
    	    "/community",
    	    "/community/**"
    	};

    	http.authorizeHttpRequests(auth -> auth

    	        /* DispatcherType 허용 -> FORWARD: 컨트롤러에서 뷰로 포워딩하는 내부 흐름 허용, ERROR  : 에러 페이지로 넘어가는 내부 흐름 허용
    	         * (Thymeleaf는 include 개념이 엔진 내부 처리라 INCLUDE는 보통 필요 없음) */
    	        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

    	        // ===== 공개 URL (비로그인 허용) =====
    	        .requestMatchers(excludeUri).permitAll()

    	        // 손영대 
    	        .requestMatchers(
    	                // 회원 공용
    	                "/member/login",
    	                "/member/registerMember",
    	                "/member/registerMemberEnd",
    	                "/member/registerCompanyMember",
    	                "/member/registerCompanyMemberEnd",
    	                "/member/registerSuccess",
    	                "/member/findAccount",
    	                "/member/dormant",
    	                "/member/dormant/send-code",
    	                "/member/dormant/verify-code",
    	                "/member/dormant/unlock",
    	                "/member/password/reset",

    	                // 비밀번호 재설정 SMS 인증
    	                "/member/password/send-code",
    	                "/member/password/verify-code",

    	                // 아이디/비밀번호 찾기 처리
    	                "/member/find/memberId",
    	                "/member/find/memberPassword",
    	                "/member/find/companyPassword",
    	                "/member/find/companyId",

    	                // 중복 체크 / 테스트 API
    	                //"/api/sms/test",
    	                "/api/members/check-memberid",
    	                "/api/members/check-email",
    	                "/api/members/check-bizno"
    	        ).permitAll()
    	        
    	        // 김서영
    	        .requestMatchers("/jobseeker/**").hasRole("MEMBER")
    	        .requestMatchers("/api/mypage/**").hasRole("MEMBER")
    	        .requestMatchers("/api/resume/**").hasRole("MEMBER")
    	        .requestMatchers("/api/companyinfo/**").hasRole("MEMBER")
    	        .requestMatchers("/api/offer/**").hasRole("MEMBER")
    	        .requestMatchers("/api/scrap/**").hasRole("MEMBER")
    	        .requestMatchers("/api/follow/**").hasRole("MEMBER")
    	        .requestMatchers("/api/recent/**").hasRole("MEMBER")


    	        // ===== 권한 필요한 URL =====
    	        /* hasRole("ADMIN") 은 내부적으로 "ROLE_ADMIN"을 찾는다. DB 권한 문자열이 ROLE_ 접두사를 포함하는지 여부에 따라 hasRole/hasAuthority 선택에 유의! */
    	        .requestMatchers("/security/special/**").hasAnyRole("ADMIN", "USER_SPECIAL")
    	        .requestMatchers("/security/admin/**").hasRole("ADMIN")
    	        .requestMatchers("/admin/**").hasRole("ADMIN")
    	        .requestMatchers("/company/**").hasRole("COMPANY")

    	        // ===== 그 외 전부 로그인(인증) 필요 (항상 맨 마지막) =====
    	        .anyRequest().authenticated()
    	);
    	
    	http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    	http.addFilterAfter(dormantAccountFilter, JwtAuthenticationFilter.class);
    	
    	http.logout(logout -> logout
                /* 로그아웃 처리 -> /security/logout 호출 시 로그아웃 수행, 세션 무효화 후 /index로 이동 */
                .logoutUrl("/security/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.invalidate();
                    }

                    jakarta.servlet.http.Cookie accessTokenCookie = new jakarta.servlet.http.Cookie("accessToken", null);
                    accessTokenCookie.setPath("/");
                    accessTokenCookie.setMaxAge(0);
                    response.addCookie(accessTokenCookie);

                    jakarta.servlet.http.Cookie refreshTokenCookie = new jakarta.servlet.http.Cookie("refreshToken", null);
                    refreshTokenCookie.setPath("/");
                    refreshTokenCookie.setMaxAge(0);
                    response.addCookie(refreshTokenCookie);

                    jakarta.servlet.http.Cookie jsessionCookie = new jakarta.servlet.http.Cookie("JSESSIONID", null);
                    jsessionCookie.setPath("/");
                    jsessionCookie.setMaxAge(0);
                    response.addCookie(jsessionCookie);
                })
                .logoutSuccessUrl("/index")
        );

        http.exceptionHandling(ex -> ex
                // 인증 실패(비로그인 접근) 시
                .authenticationEntryPoint(customAuthenticationEntryPoint())

                // 권한 실패(로그인 했지만 권한 없음) 시
                .accessDeniedHandler(customAccessDeniedHandler())
        );

        /* iframe 관련 보안 헤더 설정 -> Clickjacking 방지를 위해 Spring Security는 기본적으로 iframe을 막는다., sameOrigin(): 동일 출처(도메인)에서만 iframe 허용 */
        http.headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
        );

        return http.build();
    }

    /* 정적 리소스는 보안 필터를 "아예 타지 않도록" 제외 -> permitAll() : 필터는 타지만 인증 검사만 안함, ignoring()  : 필터 자체를 타지 않음 (정적 리소스에 흔히 사용) */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/bootstrap-4.6.2-dist/**",
                "/css/**",
                "/fullcalendar_5.10.1/**",
                "/Highcharts-10.3.1/**",
                "/images/**",
                "/jquery-ui-1.13.1.custom/**",
                "/js/**",
                "/smarteditor/**",
                "/resources/photo_upload/**"
        );
    }
}
