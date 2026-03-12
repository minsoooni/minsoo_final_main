package com.spring.app.jobseeker.domain;

import java.util.List;

import lombok.Data;

@Data
public class ResumeDTO {

    private long resumeId;             // 이력서 ID (PK, seq_resume_id)
    private String memberid;           // 작성자 회원 ID
    private Long categoryId;           // 희망 직무 카테고리
    private String regionCode;         // 희망 지역
    private String title;              // 이력서 제목
    private String address;            // 주소
    private String selfIntro;          // 자기소개
    private Long desiredSalary;        // 희망 연봉
    private String photoPath;          // 사진 경로
    private int writeStatus;           // 작성 상태 (0:임시저장, 1:작성완료)
    private int isPrimary;             // 대표 이력서 여부 (0:일반, 1:대표)
    private int allowScout;            // 제안 허용 여부 (0:허용안함, 1:허용)
    private String uploadedAt;           // 등록일
    private int isDeleted;             // 삭제 여부 (0:작성, 1:삭제)

    // === 조인으로 가져올 추가 정보 === //
    private String memberName;         // 회원 이름 (tbl_member.name)
    private String memberEmail;        // 회원 이메일 (tbl_member.email)
    private String memberPhone;        // 회원 전화번호 (tbl_member.phone)
    private String memberBirthDate;    // 회원 생년월일 (tbl_member.birth_date)
    private Integer memberGender;      // 회원 성별 (tbl_member.gender, 1:남 2:여)
    private String categoryName;       // 직무 카테고리명
    private String regionName;         // 지역명

    // === 리스트용 요약 정보 === //
    private String educationSummary;   // 학력 요약 (학교명 전공)
    private String careerSummary;      // 경력 요약 (회사명 (N건))
    private String techSummary;        // 기술스택 요약 (React, Java, ...)
    private int certificateCount;      // 자격증 수
    private int portfolioCount;        // 포트폴리오 수
    private int languageCount;         // 어학 수
    private Integer careerMonths;      // 총 경력 개월수
    private String careerCategoryName; // 경력 직무명
    private String educationLevelName; // 학력 수준명 (고등학교, 대학교 등)

    // === 1:N 하위 항목 리스트 === //
    private List<ResumeEducationDTO> educationList;
    private List<ResumeCareerDTO> careerList;
    private List<ResumeLanguageDTO> languageList;
    private List<ResumeCertificateDTO> certificateList;
    private List<ResumePortfolioDTO> portfolioList;
    private List<ResumeAwardDTO> awardList;
    private List<ResumeTechstackDTO> techstackList;
}
