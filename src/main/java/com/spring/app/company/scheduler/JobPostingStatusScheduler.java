package com.spring.app.company.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spring.app.company.service.CompanyService;

import lombok.RequiredArgsConstructor;



@Component
@RequiredArgsConstructor
public class JobPostingStatusScheduler {
	
	private final CompanyService companyService;

    // 1분마다 실행
	@Scheduled(cron = "0 * * * * *")
    public void refreshJobPostingStatuses() {
        companyService.refreshJobPostingStatuses();
        companyService.refreshBannerStatuses();
    }
}
