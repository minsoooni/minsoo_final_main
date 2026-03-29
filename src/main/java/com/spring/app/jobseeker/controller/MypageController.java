package com.spring.app.jobseeker.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.domain.ResumeDTO;
import com.spring.app.jobseeker.service.JobPostingService;
import com.spring.app.jobseeker.service.MypageService;
import com.spring.app.member.domain.MemberDTO;

@Controller
@RequestMapping("/jobseeker")
public class MypageController {

    // === 중복 상수화 === //
    private static final String KEY_ACTIVE_MENU = "activeMenu";
    private static final String KEY_MEMBER = "member";
    private static final String KEY_DEADLINE = "deadline";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_PW_ERROR = "pwError";
    private static final String REDIRECT_PROFILE_EDIT = "redirect:/jobseeker/profile/edit";
    private static final String KEY_COMMUNITY_ERROR = "communityError";

    private final MypageService mypageService;
    private final JobPostingService jobPostingService;
    private final PasswordEncoder passwordEncoder;

    public MypageController(MypageService mypageService,
                            JobPostingService jobPostingService,
                            PasswordEncoder passwordEncoder) {
        this.mypageService = mypageService;
        this.jobPostingService = jobPostingService;
        this.passwordEncoder = passwordEncoder;
    }

    
    // 마이페이지 대시보드
    // GET /jobseeker/dashboard
    @GetMapping("dashboard")
    public ModelAndView dashboard(ModelAndView mav, Principal principal) {

        mav.addObject(KEY_ACTIVE_MENU, "dashboard");

        String memberId = principal.getName();

        // === 프로필 (DB) === //
        MemberDTO member = mypageService.getMemberInfo(memberId);
        ResumeDTO resume = mypageService.getPrimaryResume(memberId);
        mav.addObject(KEY_MEMBER, member);
        mav.addObject("resume", resume);

        // === 통계 카드 (DB) === //
        Map<String, Integer> stats = mypageService.getDashboardStats(memberId);
        mav.addObject("stats", stats);

        // === 최근 지원 내역 (DB, 최근 3건) === //
        List<Map<String, Object>> recentApplications = mypageService.getRecentApplications(memberId);
        mav.addObject("recentApplications", recentApplications);

        // === 최근 본 공고 (DB, 최근 3건) === //
        List<Map<String, Object>> recentPosts = mypageService.getRecentViewedPosts(memberId);
        mav.addObject("recentPosts", recentPosts);

        // === 맞춤 추천 공고 (JobPostingService 재사용) === //
        List<JobPostingListDTO> recommendedPostDTOs = new ArrayList<>();
        boolean hasResume = jobPostingService.hasPrimaryResume(memberId);
        if (hasResume) {
            recommendedPostDTOs = jobPostingService.getRecommendedJobPostings(memberId);
        }

        
        List<Map<String, String>> recommendedPosts = new ArrayList<>();
        for (JobPostingListDTO dto : recommendedPostDTOs) {
            Map<String, String> post = new HashMap<>();
            post.put("id", String.valueOf(dto.getJobId()));
            post.put("title", dto.getTitle());
            post.put("companyName", dto.getCompanyName());
            post.put("region", dto.getRegionName() != null ? dto.getRegionName() : "");
            post.put("salary", dto.getSalary() != null ? String.format("%,d만원", dto.getSalary()) : "협의");

            // 매칭 점수 (항목별)
            int regionScore = dto.getRegionScore() != null ? dto.getRegionScore() : 0;
            int categoryScore = dto.getCategoryScore() != null ? dto.getCategoryScore() : 0;
            int salaryScore = dto.getSalaryScore() != null ? dto.getSalaryScore() : 0;
            int techScore = dto.getTechScore() != null ? dto.getTechScore() : 0;
            int totalRaw = regionScore + categoryScore + salaryScore + techScore;
            // 만점 = 5(지역) + 3(직무) + 2(연봉) + 기술(실제 점수와 4 중 큰 값)
            int maxScore = 5 + 3 + 2 + Math.max(techScore, 4);
            int matchPercent = maxScore > 0 ? Math.min((int) Math.round((double) totalRaw / maxScore * 100), 100) : 0;
            post.put("matchScore", String.valueOf(matchPercent));
            post.put("regionScore", String.valueOf(regionScore));
            post.put("categoryScore", String.valueOf(categoryScore));
            post.put("salaryScore", String.valueOf(salaryScore));
            post.put("techScore", String.valueOf(techScore));
            post.put("matchedSkills", dto.getMatchedSkills() != null ? dto.getMatchedSkills() : "");

            // D-day 계산
            if (dto.getDeadlineAt() != null && !"".equals(dto.getDeadlineAt())) {
                try {
                    java.time.LocalDate deadlineDate = java.time.LocalDate.parse(dto.getDeadlineAt().substring(0, 10));
                    long dday = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), deadlineDate);
                    post.put(KEY_DEADLINE, dday >= 0 ? "D-" + dday : "마감");
                } catch (Exception e) {
                    post.put(KEY_DEADLINE, dto.getDeadlineAt());
                }
            } else {
                post.put(KEY_DEADLINE, "상시");
            }
            recommendedPosts.add(post);
        }
        mav.addObject("recommendedPosts", recommendedPosts);

        mav.setViewName("jobseeker/mypage/dashboard");
        return mav;
    }

   
    // 프로필 조회 페이지
    // GET /jobseeker/profile
    @GetMapping("profile")
    public ModelAndView profile(ModelAndView mav, Principal principal) {

        String memberId = principal.getName();

        MemberDTO member = mypageService.getMemberInfo(memberId);
        ResumeDTO resume = mypageService.getPrimaryResume(memberId);

        mav.addObject(KEY_MEMBER, member);
        mav.addObject("resume", resume);
        mav.addObject(KEY_ACTIVE_MENU, "profile");
        mav.setViewName("jobseeker/mypage/profile");
        return mav;
    }

   
    // 프로필 수정 폼 페이지
    // GET /jobseeker/profile/edit
    @GetMapping("profile/edit")
    public ModelAndView profileEdit(ModelAndView mav, Principal principal) {

        String memberId = principal.getName();

        MemberDTO member = mypageService.getMemberInfo(memberId);
        ResumeDTO resume = mypageService.getPrimaryResume(memberId);

        mav.addObject(KEY_MEMBER, member);
        mav.addObject("resume", resume);
        mav.addObject(KEY_ACTIVE_MENU, "profile");
        mav.setViewName("jobseeker/mypage/profileEdit");
        return mav;
    }

   
    // 프로필 수정 처리
    // POST /jobseeker/profile/edit
    @PostMapping("profile/edit")
    public String profileUpdate(
            @RequestParam("name") String name,
            @RequestParam("birthDate") String birthDate,
            @RequestParam("gender") int gender,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String memberId = principal.getName();

        MemberDTO dto = new MemberDTO();
        dto.setMemberId(memberId);
        dto.setName(name);
        dto.setBirthDate(LocalDate.parse(birthDate));
        dto.setGender(gender);
        dto.setEmail(email);
        dto.setPhone(phone);

        int result = mypageService.updateProfile(dto);

        if (result > 0) {
            redirectAttributes.addFlashAttribute(KEY_MESSAGE, "프로필이 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute(KEY_MESSAGE, "프로필 수정에 실패했습니다.");
        }

        return "redirect:/jobseeker/profile";
    }

   
    // 비밀번호 수정 처리
    // POST /jobseeker/profile/password
    @PostMapping("profile/password")
    public String passwordUpdate(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("newPasswordConfirm") String newPasswordConfirm,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String memberId = principal.getName();

        // 1) 새 비밀번호 일치 확인
        if (!newPassword.equals(newPasswordConfirm)) {
            redirectAttributes.addFlashAttribute(KEY_PW_ERROR, "새 비밀번호가 일치하지 않습니다.");
            return REDIRECT_PROFILE_EDIT;
        }

        // 2) 현재 비밀번호 확인
        String storedPassword = mypageService.getPassword(memberId);
        if (!passwordEncoder.matches(currentPassword, storedPassword)) {
            redirectAttributes.addFlashAttribute(KEY_PW_ERROR, "현재 비밀번호가 올바르지 않습니다.");
            return REDIRECT_PROFILE_EDIT;
        }

        // 3) 비밀번호 변경
        String encodedPassword = passwordEncoder.encode(newPassword);
        int result = mypageService.updatePassword(memberId, encodedPassword);

        if (result > 0) {
            redirectAttributes.addFlashAttribute(KEY_MESSAGE, "비밀번호가 변경되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute(KEY_PW_ERROR, "비밀번호 변경에 실패했습니다.");
        }

        return REDIRECT_PROFILE_EDIT;
    }

   
    // 커뮤니티 인증 등록/변경
    // POST /jobseeker/profile/community
    @PostMapping("profile/community")
    public String communityVerify(
            @RequestParam("communityCompanyName") String communityCompanyName,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String memberId = principal.getName();
        String companyName = communityCompanyName.trim();

        if (companyName.isEmpty()) {
            redirectAttributes.addFlashAttribute(KEY_COMMUNITY_ERROR, "직장명을 입력해주세요.");
            return REDIRECT_PROFILE_EDIT;
        }

        int result = mypageService.updateCommunityCompanyName(memberId, companyName);

        if (result > 0) {
            redirectAttributes.addFlashAttribute(KEY_MESSAGE, "커뮤니티 인증이 완료되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute(KEY_COMMUNITY_ERROR, "커뮤니티 인증에 실패했습니다.");
        }

        return REDIRECT_PROFILE_EDIT;
    }

   
    // 커뮤니티 인증 취소
    // POST /jobseeker/profile/community/cancel
    @PostMapping("profile/community/cancel")
    public String communityCancelVerify(
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String memberId = principal.getName();

        int result = mypageService.updateCommunityCompanyName(memberId, null);

        if (result > 0) {
            redirectAttributes.addFlashAttribute(KEY_MESSAGE, "커뮤니티 인증이 취소되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute(KEY_COMMUNITY_ERROR, "인증 취소에 실패했습니다.");
        }

        return REDIRECT_PROFILE_EDIT;
    }
}
