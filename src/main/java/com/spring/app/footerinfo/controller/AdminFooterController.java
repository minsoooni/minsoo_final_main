package com.spring.app.footerinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminFooterController {

    @GetMapping("/faq")
    public String faq() {
        return "adminfooterinfo/faq";
    }

    @GetMapping("/inquiry")
    public String inquiry() {
        return "adminfooterinfo/inquiry";
    }

    @GetMapping("/policy/terms")
    public String terms() {
        return "adminfooterinfo/terms";
    }

    @GetMapping("/policy/privacy")
    public String privacy() {
        return "adminfooterinfo/privacy";
    }

    @GetMapping("/policy/recruit-terms")
    public String recruitTerms() {
        return "adminfooterinfo/recruit_terms";
    }

    @GetMapping("/ad/inquiry")
    public String advertise() {
        return "adminfooterinfo/ad_inquiry";
    }
    
    @GetMapping("/policy/email-no-collect")
    public String emailNoCollect() { return "adminfooterInfo/email-no-collect"; }

    @GetMapping("/guide")
    public String guide() { return "adminfooterInfo/guide"; }

    @GetMapping("/company/info")
    public String companyInfo() { return "adminfooterInfo/company-info"; }
    
    
}