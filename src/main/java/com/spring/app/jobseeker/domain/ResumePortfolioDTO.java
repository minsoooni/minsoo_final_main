package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class ResumePortfolioDTO {

    private long portfolioId;          // 포트폴리오 ID (PK, seq_resume_portfolio_id)
    private long resumeId;             // 이력서 ID (FK)
    private int portfolioType;         // 0: 링크, 1: 파일
    private String link;               // URL 링크
    private String filepath;           // 첨부파일 경로
    private String originalFilename;   // 첨부파일 원본명
    private String portfolioTitle;     // 포트폴리오 제목
    private String portfolioUrl;       // URL (화면용 별칭)
    private String portfolioDesc;      // 설명
    private String createdAt;          // 등록일
    private int sort;                  // 정렬 순서
}
