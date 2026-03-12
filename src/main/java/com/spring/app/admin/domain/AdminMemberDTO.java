package com.spring.app.admin.domain;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminMemberDTO {
    private String    memberId;     // TBL_MEMBER.MEMBERID
    private String    name;         // TBL_MEMBER.NAME
    private String    email;        // TBL_MEMBER.EMAIL
    private Integer   status;       // TBL_MEMBER.STATUS (0=활성, 1=정지, 2=탈퇴)
    private LocalDate createdAt;    // TBL_MEMBER.CREATED_AT
    private LocalDate lastLoginAt;  // TBL_MEMBER.LAST_LOGIN_AT
    private String    phone;
    private Integer   gender;
    private Integer   type;
    private LocalDate birthDate;
    private LocalDate dormantAt;
}