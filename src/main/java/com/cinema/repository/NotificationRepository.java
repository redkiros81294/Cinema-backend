package com.cinema.repository;

import com.cinema.model.entity.Notification;
import com.cinema.model.entity.NotificationStatus;
import com.cinema.model.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);
    
    List<Notification> findByUserIdAndTypeAndStatusOrderByCreatedAtDesc(
        Long userId, NotificationType type, NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = ?1 AND n.createdAt >= ?2 ORDER BY n.createdAt DESC")
    List<Notification> findRecentByUserId(Long userId, LocalDateTime since);
    
    long countByUserIdAndStatus(Long userId, NotificationStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.emailSent = false AND n.createdAt <= ?1")
    List<Notification> findPendingEmailNotifications(LocalDateTime before);
    
    @Query("SELECT n FROM Notification n WHERE n.smsSent = false AND n.createdAt <= ?1")
    List<Notification> findPendingSmsNotifications(LocalDateTime before);
} 