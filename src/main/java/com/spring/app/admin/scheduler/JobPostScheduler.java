package com.spring.app.admin.scheduler;

import com.spring.app.admin.service.AdminJobPostService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobPostScheduler {

    private final AdminJobPostService adminJobPostService;

    

    @PostConstruct //
    public void init() {
        adminJobPostService.updateExpiredJobsClosed();
    }
    
    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void closeExpiredJobs() {
        adminJobPostService.updateExpiredJobsClosed();
    }
}