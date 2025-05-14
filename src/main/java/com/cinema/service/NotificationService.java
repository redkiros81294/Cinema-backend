package com.cinema.service;

import com.cinema.dto.notification.NotificationRequest;
import com.cinema.dto.notification.NotificationResponse;
import com.cinema.model.entity.NotificationStatus;
import com.cinema.model.entity.NotificationType;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    
    List<NotificationResponse> getUserNotifications(Long userId, NotificationStatus status);
    
    List<NotificationResponse> getUserNotificationsByType(Long userId, NotificationType type, NotificationStatus status);
    
    List<NotificationResponse> getRecentNotifications(Long userId, int hours);
    
    NotificationResponse markAsRead(Long notificationId);
    
    NotificationResponse markAsArchived(Long notificationId);
    
    long getUnreadCount(Long userId);
} 