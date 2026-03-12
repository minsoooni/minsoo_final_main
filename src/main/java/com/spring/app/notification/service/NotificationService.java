package com.spring.app.notification.service;

import java.util.List;

import com.spring.app.notification.domain.NotificationDTO;

public interface NotificationService {

    List<NotificationDTO> getMyNotifications(String memberId);

    int getUnreadNotificationCount(String memberId);

    int readNotification(Long notiId);
}