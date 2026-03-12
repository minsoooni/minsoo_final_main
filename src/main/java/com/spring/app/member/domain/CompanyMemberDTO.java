package com.spring.app.member.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CompanyMemberDTO {

    // =========================== MEMBER 테이블 ===========================
    private String memberId; // VARCHAR2(50)  NOT NULL  회원 ID
    private String password; // VARCHAR2(255)  NOT NULL  비밀번호 (암호화 저장)
    private String name; // VARCHAR2(50)  NOT NULL  이름
    private String email; // VARCHAR2(100)  NOT NULL UNIQUE  이메일
    private String phone; // VARCHAR2(20)  NOT NULL  전화번호
    private LocalDate birthDate; // DATE NOT NULL (TBL_MEMBER.BIRTH_DATE)
    private Integer gender; // NUMBER(1) NOT NULL (TBL_MEMBER.GENDER)
    private Integer type; // NUMBER(1)  NOT NULL  회원 유형 (0:기업, 1:구직자, 2:관리자)
    private Integer status; // NUMBER(1)  NOT NULL DEFAULT 0  계정 상태 (0:활성, 1:정지, 2:탈퇴, 3:휴면)
    private Integer mustChangePasswordYn; // NUMBER(1)  NOT NULL DEFAULT 0  비밀번호 변경 여부 (0:정상, 1:변경필요)
    private String passwordConfirm;
    
    // =========================== COMPANY 테이블 ===========================
    private String companyName; // VARCHAR2(100)  NOT NULL  회사명
    private String bizNo; // VARCHAR2(12)  NOT NULL UNIQUE  사업자번호     
    private String industryCode; // VARCHAR2(20)  NOT NULL  업종 코드
    private String ceoName; // VARCHAR2(50)  NULL  대표자명 
    private String openDate; // DATE  NULL  설립일
    private String homepageUrl; // VARCHAR2(300)  NULL  홈페이지
    private String description; // CLOB  NULL  기업 소개
    private String zipCode; // VARCHAR2(10)  NULL  우편번호
    private String addr1; // VARCHAR2(200)  NULL  주소1
    private String addr2; // VARCHAR2(200)  NULL  주소2
    private Double lat; // NUMBER(10,7)  NULL  위도
    private Double lng; // NUMBER(10,7)  NULL  경도
}