package com.spring.app.company.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.app.company.domain.CompanyProfileDTO;
import com.spring.app.company.domain.CompanyProfileUpdateDTO;
import com.spring.app.company.domain.CompanyProfileUpdateResponseDTO;
import com.spring.app.company.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Company - Profile REST API (AJAX/Swagger)
 */
@Tag(name = "Company - Profile API", description = "기업 프로필 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company/profile/api")
public class CompanyProfileApiController {
    private final CompanyService service;
    
    @Operation(summary = "기업 프로필 조회", description = "로그인한 기업 회원의 프로필 정보를 조회한다.")
    @GetMapping("/detail")
    public CompanyProfileDTO getCompanyProfile(Authentication authentication) {
        String memberId = authentication.getName();
        return service.getCompanyProfile(memberId);
    }
    
    
    @Operation(summary = "기본 정보 수정", description = "기업 기본 정보를 수정한다.")
    @PostMapping("/update/basic")
    public CompanyProfileUpdateResponseDTO updateBasicProfile(@RequestBody CompanyProfileUpdateDTO dto,
                                                              Authentication authentication) {
        String memberId = authentication.getName();
        dto.setMemberId(memberId);
        return service.updateBasicProfile(dto);
    }

    @Operation(summary = "주소 정보 수정", description = "기업 주소 정보를 수정한다.")
    @PostMapping("/update/address")
    public CompanyProfileUpdateResponseDTO updateAddressProfile(@RequestBody CompanyProfileUpdateDTO dto,
                                                                Authentication authentication) {
        String memberId = authentication.getName();
        dto.setMemberId(memberId);
        return service.updateAddressProfile(dto);
    }

    @Operation(summary = "기업 소개 수정", description = "기업 소개 정보를 수정한다.")
    @PostMapping("/update/intro")
    public CompanyProfileUpdateResponseDTO updateIntroProfile(@RequestBody CompanyProfileUpdateDTO dto,
                                                              Authentication authentication) {
        String memberId = authentication.getName();
        dto.setMemberId(memberId);
        return service.updateIntroProfile(dto);
    }
    
    
    
    @PostMapping("/logo")
    public CompanyProfileUpdateResponseDTO uploadCompanyLogo(
            @RequestParam("logoFile") MultipartFile logoFile,
            Authentication authentication) throws Exception {

        String memberId = authentication.getName();
        return service.uploadCompanyLogo(memberId, logoFile);
    }
}
