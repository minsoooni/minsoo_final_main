package com.spring.app.company.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.company.domain.TalentFilterResponseDTO;
import com.spring.app.company.domain.TalentResumeDetailDTO;
import com.spring.app.company.domain.TalentSearchConditionDTO;
import com.spring.app.company.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company/talent")
public class CompanyTalentApiController {

    private final CompanyService service;

    // 인재검색 필터 데이터 조회
    @GetMapping("/filters")
    public ResponseEntity<TalentFilterResponseDTO> getTalentFilters() {

        TalentFilterResponseDTO dto = new TalentFilterResponseDTO();
        dto.setJobCategoryList(service.getJobCategoryList());
        dto.setSkillCategoryList(service.getSkillCategoryList());
        dto.setSkillList(service.getSkillList());

        return ResponseEntity.ok(dto);
    }
    
    
    // 공개 대표이력서 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getTalentList(@ModelAttribute TalentSearchConditionDTO searchDto) {

        Map<String, Object> result = new HashMap<>();
        result.put("resumeList", service.getPublicPrimaryResumeList(searchDto));
        result.put("totalCount", service.getPublicPrimaryResumeCount(searchDto));

        return ResponseEntity.ok(result);
    }
    
    // 공개 대표이력서 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<?> getTalentDetail(@RequestParam("resumeId") Long resumeId) {

        TalentResumeDetailDTO dto = service.getPublicPrimaryResumeDetail(resumeId);

        if (dto == null) {
            return ResponseEntity.badRequest().body("조회 가능한 공개 이력서가 없습니다.");
        }

        return ResponseEntity.ok(dto);
    }
    
    
    
    
}