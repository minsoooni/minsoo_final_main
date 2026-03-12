package com.spring.app.company.domain;

import java.util.List;

import com.spring.app.jobseeker.domain.ResumeAwardDTO;
import com.spring.app.jobseeker.domain.ResumeCareerDTO;
import com.spring.app.jobseeker.domain.ResumeCertificateDTO;
import com.spring.app.jobseeker.domain.ResumeEducationDTO;
import com.spring.app.jobseeker.domain.ResumeLanguageDTO;
import com.spring.app.jobseeker.domain.ResumePortfolioDTO;
import com.spring.app.jobseeker.domain.ResumeTechstackDTO;

import lombok.Data;

@Data
//공개 이력서 상세 DTO
public class TalentResumeDetailDTO {
	// =========================
    // tbl_resume 기본 정보
    // =========================
    private Long resumeId;          // 이력서 PK
    private String memberid;        // 회원 ID
    private Long categoryId;        // 희망 직무 카테고리 ID
    private String regionCode;      // 희망 근무 지역 코드

    private String title;           // 이력서 제목
    private String address;         // 주소
    private String selfIntro;       // 자기소개
    private String photoPath;       // 프로필 사진 경로

    private Long desiredSalary;     // 희망 연봉
    private Integer writeStatus;    // 작성 상태
    private Integer isPrimary;      // 대표 이력서 여부
    private Integer allowScout;     // 공개 여부
    private String uploadedAt;      // 등록일 (문자열로 받아도 됨)
    private Integer isDeleted;      // 삭제 여부

    // =========================
    // 회원 정보 (tbl_member 조인)
    // =========================
    private String memberName;      // 이름
    private String memberEmail;     // 이메일
    private String memberPhone;     // 전화번호
    private String memberBirthDate; // 생년월일
    private Integer memberGender;   // 성별

    // =========================
    // 코드/마스터 조인 정보
    // =========================
    private String categoryName;    // 직무명
    private String regionName;      // 지역명

    // =========================
    // 하위 상세 리스트
    // =========================
    private List<ResumeEducationDTO> educationList;       // 학력
    private List<ResumeCareerDTO> careerList;             // 경력
    private List<ResumeLanguageDTO> languageList;         // 어학
    private List<ResumeCertificateDTO> certificateList;   // 자격증
    private List<ResumePortfolioDTO> portfolioList;       // 포트폴리오
    private List<ResumeAwardDTO> awardList;               // 수상
    private List<ResumeTechstackDTO> techstackList;       // 기술스택
}
