package com.spring.app.security.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spring.app.member.domain.MemberDTO;
import com.spring.app.member.mapper.MemberMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DormantAccountFilter extends OncePerRequestFilter {

    private final MemberMapper memberMapper;

    public DormantAccountFilter(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {

            String memberId = auth.getName();

            MemberDTO member = memberMapper.selectLoginMeta(memberId);

            if (member != null && member.getStatus() != null && member.getStatus() == 3) {

                String uri = request.getRequestURI();

                // dormant 페이지는 허용
                if (!uri.startsWith("/member/dormant")) {
                    response.sendRedirect(request.getContextPath()
                            + "/member/dormant?memberId=" + memberId);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}