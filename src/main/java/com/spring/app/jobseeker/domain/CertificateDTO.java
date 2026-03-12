package com.spring.app.jobseeker.domain;

import lombok.Data;

@Data
public class CertificateDTO {

    private String certificateCode;    // 자격증 코드
    private String certificateName;    // 자격증명
    private String issuer;             // 발급기관
}
