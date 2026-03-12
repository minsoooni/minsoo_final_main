package com.spring.app.notification.domain;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long   notiId;
    private String fkMemberId;
    private String type;
    private String title;
    private String message;
    private String linkUrl;
    private int    isRead;
}