package com.spring.app.company.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;


@Data
//인재검색 리스트용 DTO
public class TalentResumeDTO {
	// 이력서 PK
    private Long resumeId;

    // 회원 ID
    private String memberId;

    // 이름
    private String name;

    // 이력서 제목
    private String resumeTitle;

    // 희망 직무 카테고리
    private Long categoryId;
    private String categoryName;

    // 희망 근무지
    private String regionCode;
    private String regionName;

    // 주소
    private String address;

    // 자기소개
    private String selfIntro;

    // 사진
    private String photoPath;

    // 희망 연봉
    private Long desiredSalary;

    // 업데이트일
    private Date uploadedAt;

    // 공개 여부 관련
    private Integer isPrimary;
    private Integer allowScout;
    private Integer isDeleted;

    // 화면 표시용
    private String education;
    private String career;
    private String portfolio;

    // 기술 스택 출력용
    private List<String> techStackNames;
}
