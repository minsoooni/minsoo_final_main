package com.spring.app.index.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.index.service.IndexService;
import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class IndexController {
	private final IndexService service;
	private final NotificationService notificationService;
	
	
	//=========== [메인 페이지 요청] =============//
	@GetMapping("/")
	//이제 URL에 http://localhost:9080/finalProject 만 입력하고 들어가도 /index 경로로 들어가게 됨!
	public String main() {
		return "redirect:/index";
	}
	

	@GetMapping("index")
	public String index(Model model, Principal principal) {
		
		if (principal != null) {
		    String memberId = principal.getName();

		    List<NotificationDTO> notificationList =
		            notificationService.getMyNotifications(memberId);

		    int unreadCount =
		            notificationService.getUnreadNotificationCount(memberId);

		    model.addAttribute("notificationList", notificationList);
		    model.addAttribute("unreadNotificationCount", unreadCount);
		}
		
		return "index";
		// /src/main/resources/templates/index.html 파일을 만들어야 함
	}
}