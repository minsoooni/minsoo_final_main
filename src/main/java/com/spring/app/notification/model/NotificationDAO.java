package com.spring.app.notification.model;

import com.spring.app.notification.domain.NotificationDTO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationDAO {
    int insertNotification(NotificationDTO dto);
    
    List<NotificationDTO> selectNotificationsByMemberId(@Param("fkMemberId") String fkMemberId);
    
    int countUnreadNotifications(@Param("fkMemberId") String fkMemberId);
    
    int markNotificationRead(@Param("notiId") Long notiId);
}