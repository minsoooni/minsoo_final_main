package com.spring.app.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.app.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notification/api/read/{notiId}")
    @ResponseBody
    public ResponseEntity<Void> readNotification(@PathVariable("notiId") Long notiId) {

        System.out.println("CONTROLLER >>> /notification/api/read called / notiId = " + notiId);

        int result = notificationService.readNotification(notiId);

        System.out.println("CONTROLLER >>> update result = " + result);

        return ResponseEntity.ok().build();
    }
}