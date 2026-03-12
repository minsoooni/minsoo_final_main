package com.spring.app.security.filter;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginTypeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public UsernamePasswordAuthenticationToken attemptAuthentication(HttpServletRequest request,
                                                                    HttpServletResponse response) {

        String username = obtainUsername(request); // memberid
        String password = obtainPassword(request); // passwd

        if (username == null) username = "";
        if (password == null) password = "";

        username = username.trim();

        // loginType 확인
        String loginType = request.getParameter("loginType"); // company / member
        if (loginType == null) loginType = "";

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);

        // Provider에서 꺼내볼 수 있게 details에 담기
        Map<String, Object> details = new HashMap<>();
        details.put("loginType", loginType);
        authRequest.setDetails(details);

        setDetails(request, authRequest);

        return (UsernamePasswordAuthenticationToken) this.getAuthenticationManager().authenticate(authRequest);
    }
}