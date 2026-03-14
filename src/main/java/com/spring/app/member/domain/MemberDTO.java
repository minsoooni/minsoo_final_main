package com.spring.app.member.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MemberDTO {
    
    private String memberId; // VARCHAR2(50)  NOT NULL  회원 ID
    private String email; // VARCHAR2(100)  NOT NULL UNIQUE  이메일
    private String password; // VARCHAR2(255)  NOT NULL  비밀번호 (암호화 저장)
    private String name; // VARCHAR2(50)  NOT NULL  이름
    private String phone; // VARCHAR2(20)  NOT NULL  전화번호
    private Integer type; // NUMBER(1)  NOT NULL  회원 유형 (0:기업, 1:구직자, 2:관리자)
    private Integer status; // NUMBER(1)  NOT NULL DEFAULT 0  계정 상태 (0:활성, 1:정지, 2:탈퇴, 3:휴면)
    private LocalDateTime lastLoginAt; // DATE  NULL  마지막 로그인
    private LocalDateTime dormantAt;    // DATE  NULL  휴면전환일
    private LocalDateTime createdAt;     // DATE  NOT NULL DEFAULT SYSDATE  가입일
    private LocalDateTime updatedAt;     // DATE  NOT NULL  수정일
    private Integer mustChangePasswordYn;     // NUMBER(1)  NOT NULL DEFAULT 0  비밀번호 변경 여부 (0:정상, 1:변경필요)
    private LocalDate birthDate;    // DATE  NULL  생년월일
    private Integer gender;    // NUMBER(1)  NULL  성별 (1:남, 2:여)
    private String communityCompanyName; // VARCHAR2(100)  NULL  커뮤니티 인증 기업명

}