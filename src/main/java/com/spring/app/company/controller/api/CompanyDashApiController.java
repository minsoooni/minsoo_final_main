package com.spring.app.company.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.company.domain.CompanyDashboardDTO;
import com.spring.app.company.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Company - Job REST API (AJAX/Swagger)
 */
@Tag(name = "Company - DashBoard API", description = "기업 대시보드 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company/dashboard/api")
public class CompanyDashApiController {
    private final CompanyService service;
    
    
    @Operation(summary = "대시보드 전체 조회", description = "로그인한 기업 회원의 대시보드 데이터를 조회한다.")
    @GetMapping("/summary")
    public CompanyDashboardDTO getDashboardSummary(Authentication authentication) {
        String memberId = authentication.getName();
        return service.getCompanyDashboard(memberId);
    }
    
    
}
