package com.spring.app.jobseeker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.jobseeker.domain.JobCategoryDTO;
import com.spring.app.jobseeker.domain.RegionDTO;
import com.spring.app.jobseeker.domain.ResumeDTO;
import com.spring.app.jobseeker.service.ResumeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/jobseeker/resume")
@PreAuthorize("isAuthenticated()")
public class ResumeController {

    private final ResumeService resumeService;

    private static final int MAX_RESUME_COUNT = 5; // 이력서 최대 개수

    // 이력서 목록
    @GetMapping("/list")
    public ModelAndView list(Authentication authentication) {

        String memberId = (String) authentication.getPrincipal();

        ModelAndView mav = new ModelAndView("jobseeker/resume/list");
        List<ResumeDTO> resumeList = resumeService.selectResumeListByMember(memberId);
        mav.addObject("resumeList", resumeList);
        mav.addObject("maxResumeCount", MAX_RESUME_COUNT);
        
        return mav;
    }

    // 이력서 신규 작성 폼
    @GetMapping("/form")
    public ModelAndView form(Authentication authentication) {

        String memberId = (String) authentication.getPrincipal();

        // 이력서 개수 제한 체크
        int resumeCount = resumeService.selectResumeCountByMember(memberId);
        if (resumeCount >= MAX_RESUME_COUNT) {
        	return new ModelAndView("redirect:/jobseeker/resume/list");            
        }

        ModelAndView mav = new ModelAndView("jobseeker/resume/form");

        // 로그인한 회원 프로필 조회 (이름, 생년월일, 성별, 이메일, 전화번호)
        Map<String, Object> member = resumeService.selectMemberProfile(memberId);
        mav.addObject("member", member);

        List<JobCategoryDTO> categoryList = resumeService.selectJobCategoryList();
        mav.addObject("categoryList", categoryList);
        List<RegionDTO> regionList = resumeService.selectRegionList();
        mav.addObject("regionList", regionList);

        // 기존 이력서 목록 (불러오기용)
        List<ResumeDTO> myResumeList = resumeService.selectResumeListByMember(memberId);
        mav.addObject("myResumeList", myResumeList);

        return mav;
    }

    // 이력서 수정 폼
    @GetMapping("/form/{resumeId}")
    public ModelAndView editForm(@PathVariable("resumeId") long resumeId,
                                 Authentication authentication) {

        String memberId = (String) authentication.getPrincipal();

        ModelAndView mav = new ModelAndView("jobseeker/resume/form");
        ResumeDTO resume = resumeService.selectResumeOne(resumeId);
        mav.addObject("resume", resume);

        // 로그인한 회원 프로필 조회 (이름, 생년월일, 성별, 이메일, 전화번호)
        Map<String, Object> member = resumeService.selectMemberProfile(memberId);
        mav.addObject("member", member);

        List<JobCategoryDTO> categoryList = resumeService.selectJobCategoryList();
        mav.addObject("categoryList", categoryList);
        List<RegionDTO> regionList = resumeService.selectRegionList();
        mav.addObject("regionList", regionList);
        return mav;
    }

    // 이력서 상세보기
    @GetMapping("/detail/{resumeId}")
    public ModelAndView detail(@PathVariable("resumeId") long resumeId,
                               Authentication authentication) {

        ModelAndView mav = new ModelAndView("jobseeker/resume/detail");
        ResumeDTO resume = resumeService.selectResumeOne(resumeId);
        mav.addObject("resume", resume);
        return mav;
    }

    // 이력서 삭제 (POST)
    @PostMapping("/delete")
    public String delete(long resumeId,
                         Authentication authentication) {

        String memberId = (String) authentication.getPrincipal();
        resumeService.deleteResume(resumeId, memberId);
        return "redirect:/jobseeker/resume/list";
    }
}