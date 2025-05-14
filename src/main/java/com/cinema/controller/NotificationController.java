package com.cinema.controller;

import com.cinema.dto.notification.NotificationRequest;
import com.cinema.dto.notification.NotificationResponse;
import com.cinema.model.entity.NotificationStatus;
import com.cinema.model.entity.NotificationType;
import com.cinema.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new notification")
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @GetMapping("/user")
    @Operation(summary = "Get user notifications")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "UNREAD") NotificationStatus status) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, status));
    }

    @GetMapping("/user/type/{type}")
    @Operation(summary = "Get user notifications by type")
    public ResponseEntity<List<NotificationResponse>> getUserNotificationsByType(
            @RequestAttribute("userId") Long userId,
            @PathVariable NotificationType type,
            @RequestParam(defaultValue = "UNREAD") NotificationStatus status) {
        return ResponseEntity.ok(notificationService.getUserNotificationsByType(userId, type, status));
    }

    @GetMapping("/user/recent")
    @Operation(summary = "Get recent user notifications")
    public ResponseEntity<List<NotificationResponse>> getRecentNotifications(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(notificationService.getRecentNotifications(userId, hours));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Mark notification as archived")
    public ResponseEntity<NotificationResponse> markAsArchived(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsArchived(id));
    }

    @GetMapping("/user/unread/count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<Long> getUnreadCount(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }
} 