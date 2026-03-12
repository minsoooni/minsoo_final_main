package com.spring.app.company.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.app.common.domain.JobCategoryDTO;
import com.spring.app.common.domain.RegionDTO;
import com.spring.app.company.domain.JobPostingDTO;
import com.spring.app.company.domain.JobPostingEditResponseDTO;
import com.spring.app.company.domain.JobPostingUpdateRequestDTO;
import com.spring.app.company.service.CompanyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * Company - Job REST API (AJAX/Swagger)
 */
@Tag(name = "Company - Job API", description = "기업 채용공고 관련 REST API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/company/api")
public class CompanyJobApiController {
    private final CompanyService service;
    
    
    // ====== Lookups (job category / region) ======
    @Operation(summary = "직무 중분류 목록 조회", description = "대분류(parentId)에 속한 중분류 목록을 조회한다.")
    @GetMapping("/job/categories/children")
    public List<JobCategoryDTO> jobCategoryChildren(@RequestParam("parentId") @NotNull Long parentId) {
        return service.getChildren(parentId);
    }

    @Operation(summary = "지역 하위 목록 조회", description = "상위 지역 코드(parentCode)에 속한 하위 지역 목록을 조회한다.")
    @GetMapping("/region/children")
    public List<RegionDTO> regionChildren(@RequestParam("parentCode") @NotBlank String parentCode) {
        return service.getRegionChildren(parentCode);
    }

    // ====== Job CRUD ======
    @Operation(summary = "채용공고 1건 조회", description = "jobId로 채용공고 상세를 조회한다. (수정 화면 prefill 등에 사용)")
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<JobPostingDTO> getJob(@PathVariable("jobId") long jobId) {
        JobPostingDTO dto = service.getJobPostingOne(jobId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
    

    
    //채용공고 등록하기
    @Operation(summary = "채용공고 등록", description = "채용공고를 등록한다.")
    @PostMapping("/jobs")
    public ResponseEntity<Map<String, Object>> createJob(
    		@RequestBody JobPostingUpdateRequestDTO req,
    		Authentication authentication) {
    	
    	JobPostingDTO dto = req.getJob();
    	
    	//로그인 기업ID 넣어주기
    	String loginMemberId = authentication.getName();
    	dto.setMemberId(loginMemberId);
    	
    	int n = service.insertJobPosting(dto, req.getSkillIds());
    	
    	// 성공이면 보통 jobId도 내려주는 게 좋음 (리다이렉트/상세이동용)
    	// 여기서는 간단히 success만
    	return ResponseEntity.ok(Map.of("success", n == 1));
    }

    
    
    
    //채용공고 수정을 위한 기존 데이터 불러오기
    @Operation(summary = "기존 채용공고 데이터 조회", description = "기존 jobId의 채용공고 데이터를 조회한다.")
    @GetMapping("/jobs/{jobId}/edit")
    public JobPostingEditResponseDTO getForEdit(@PathVariable("jobId") long jobId) {
        return service.getJobPostingForEdit(jobId);
    }
    
    //변경된 공고 내용을 수정해주기
    @Operation(summary = "채용공고 수정", description = "jobId의 채용공고를 수정한다.")
    @PutMapping("/jobs/{jobId}")
    public ResponseEntity<Map<String, Object>> updateJob(
            @PathVariable("jobId") long jobId,
            @RequestBody JobPostingUpdateRequestDTO req) {

        JobPostingDTO dto = req.getJob();
        dto.setJobId(jobId);

        int n = service.updateJobPosting(dto, req.getSkillIds());
        return ResponseEntity.ok(Map.of("success", n == 1));
    }

    //공고 삭제하기
    @Operation(summary = "채용공고 삭제", description = "jobId의 채용공고를 삭제(소프트삭제)한다.")
    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable("jobId") Long jobId) {
        int n = service.deleteJobPosting(jobId);
        return ResponseEntity.ok(Map.of("success", n == 1));
    }
}
