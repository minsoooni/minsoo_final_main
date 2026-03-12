package com.spring.app.admin.domain;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminReportDTO {
	private String reportedMemberId;
    private Long    reportId;
    private Integer targetType;    // 1=채용공고, 2=게시글, 3=댓글
    private Long    targetId; //
    private String  memberId;
    private Long    reasonId;
    private String  reasonName;    // REPORT_REASON JOIN
    private String  reportContent;
    private String status; // 0=처리중, 1=승인 2=반려
    private LocalDate createdAt;
    private String  targetTitle;   // CASE WHEN으로 가져오는 제목
    private String processReason;
    private LocalDate processAt;
    
    private int reportCount;

    private Long commentPostId;

	private String reporterMemberId;
}
