package com.spring.app.admin.domain;


import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminCompanyDTO {

    private String  memberId;       // MEMBER.MEMBERID
    private String  companyName;    // COMPANY3.COMPANY_NAME
    private String  bizNo;          // COMPANY3.BIZ_NO
    private String  industryCode;   // COMPANY3.INDUSTRY_CODE
    private Integer status;        // MEMBER.STATUS (ACTIVE / SUSPENDED / WITHDRAWN) --> 0, 1, 2
    private String  approvedYn;     // COMPANY3.APPROVED_YN (APPROVED / PENDING / REJECTED) --> 0, 1, 2
    private String  rejectReason;   // COMPANY3.REJECT_REASON (거절 사유)
    private int     postCount;      // job_posting COUNT
    private LocalDate createdAt;    // MEMBER.CREATED_AT
    private String    ceoName;       // CEO_NAME
    private String    addr1;         // ADDR1
    private String    addr2;         // ADDR2
    private String    zipCode;       // ZIP_CODE
    private LocalDate approvalAt;    // APPROVAL_AT
}
