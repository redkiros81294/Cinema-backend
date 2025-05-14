package com.cinema.service.impl;

import com.cinema.dto.notification.NotificationRequest;
import com.cinema.dto.notification.NotificationResponse;
import com.cinema.model.entity.Notification;
import com.cinema.model.entity.NotificationStatus;
import com.cinema.model.entity.NotificationType;
import com.cinema.model.entity.User;
import com.cinema.repository.NotificationRepository;
import com.cinema.repository.UserRepository;
import com.cinema.service.NotificationService;
import com.cinema.exception.BusinessException;
import com.cinema.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        validateNotificationRequest(request);

        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        notification = notificationRepository.save(notification);
        return convertToResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId, NotificationStatus status) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotificationsByType(Long userId, NotificationType type, NotificationStatus status) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return notificationRepository.findByUserIdAndTypeAndStatusOrderByCreatedAtDesc(userId, type, status)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getRecentNotifications(Long userId, int hours) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (hours <= 0) {
            throw new BusinessException("Hours must be greater than 0", "INVALID_HOURS");
        }
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return notificationRepository.findRecentByUserId(userId, since)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        if (notification.getStatus() == NotificationStatus.READ) {
            throw new BusinessException("Notification is already marked as read", "ALREADY_READ");
        }
        
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        
        return convertToResponse(notificationRepository.save(notification));
    }

    @Override
    public NotificationResponse markAsArchived(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        if (notification.getStatus() == NotificationStatus.ARCHIVED) {
            throw new BusinessException("Notification is already archived", "ALREADY_ARCHIVED");
        }
        
        notification.setStatus(NotificationStatus.ARCHIVED);
        
        return convertToResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    private void validateNotificationRequest(NotificationRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException("Notification title is required", "INVALID_TITLE");
        }
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new BusinessException("Notification message is required", "INVALID_MESSAGE");
        }
        if (request.getType() == null) {
            throw new BusinessException("Notification type is required", "INVALID_TYPE");
        }
    }

    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
} 