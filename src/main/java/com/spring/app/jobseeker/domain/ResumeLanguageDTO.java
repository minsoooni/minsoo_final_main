package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class ResumeLanguageDTO {

    private long languageId;           // 어학 ID (PK, seq_resume_language_id)
    private long resumeId;             // 이력서 ID (FK)
    private String languageName;       // 언어명 (예: 영어, 일본어)
    private String testname;           // 시험명 (예: TOEIC, JLPT)
    private String score;              // 점수/등급
    private String acquiredDate;       // 취득일
}
