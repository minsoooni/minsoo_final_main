package com.spring.app.admin.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminJobPostDTO {
    private Long jobId;
    private String companyName;
    private String title;
    private String location;
    private Long salary;
    private int viewCount;
    private int applicantCount;
    private String createdAt;
    private String status;
    private Integer isHidden; // 노출 여부
    private String jobCategory;
    private String skills;
    private LocalDateTime deadlineAt;
    private LocalDateTime updatedAt;
    
   
}