package com.spring.app.index.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.index.service.IndexService;
import com.spring.app.jobseeker.domain.JobPostingListDTO;
import com.spring.app.jobseeker.service.JobPostingService;
import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class IndexController {
	
	private final IndexService indexService;
	private final NotificationService notificationService;
	private final JobPostingService jobPostingService;
	
	//=========== [메인 페이지 요청] =============//
	@GetMapping("/")
	//이제 URL에 http://localhost:9080/finalProject 만 입력하고 들어가도 /index 경로로 들어가게 됨!
	public String main() {
		return "redirect:/index";
	}
	

	@GetMapping("index")
	public String index(Model model, Principal principal) {
		
		String memberId = null;
		
		if (principal != null) {
			memberId = principal.getName();

			List<NotificationDTO> notificationList =
					notificationService.getMyNotifications(memberId);

			int unreadCount =
					notificationService.getUnreadNotificationCount(memberId);

			model.addAttribute("notificationList", notificationList);
			model.addAttribute("unreadNotificationCount", unreadCount);
		}

		model.addAttribute("categoryList", indexService.getParentJobCategories());

		List<JobPostingListDTO> popularJobList = jobPostingService.getPopularJobPostings(4);
		model.addAttribute("popularJobList", popularJobList);
		
		List<Map<String, Object>> hotCompanyList = indexService.getHotCompanies();
		model.addAttribute("hotCompanyList", hotCompanyList);
		
		List<Map<String, Object>> mainBannerList = indexService.getMainBannerList();
		model.addAttribute("mainBannerList", mainBannerList);
		
		return "index";
	}
}