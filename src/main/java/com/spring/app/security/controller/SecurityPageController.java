package com.spring.app.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityPageController {

    @GetMapping("/security/noAuthenticated")
    public String noAuthenticated() {
        return "security/noAuthenticated"; // templates/security/noAuthenticated.html
    }
}