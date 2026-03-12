package com.spring.app.notification.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.app.notification.domain.NotificationDTO;
import com.spring.app.notification.model.NotificationDAO;

@Service
public class NotificationService_imple implements NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    @Override
    public List<NotificationDTO> getMyNotifications(String memberId) {
        return notificationDAO.selectNotificationsByMemberId(memberId);
    }
    
    @Override
    public int getUnreadNotificationCount(String memberId) {
        return notificationDAO.countUnreadNotifications(memberId);
    }
    
    @Override
    public int readNotification(Long notiId) {
        int result = notificationDAO.markNotificationRead(notiId);
        System.out.println("SERVICE >>> readNotification result = " + result + " / notiId = " + notiId);
        return result;
    }
}