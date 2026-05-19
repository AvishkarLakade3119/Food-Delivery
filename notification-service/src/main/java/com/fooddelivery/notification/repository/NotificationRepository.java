package com.fooddelivery.notification.repository;

import com.fooddelivery.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by user ID
    List<Notification> findByUserId(Long userId);
    
    // Find notifications by user ID ordered by creation date (latest first)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find notifications by type
    List<Notification> findByType(String type);
    
    // Find notifications by status
    List<Notification> findByStatus(String status);
    
    // Find notifications by user ID and type
    List<Notification> findByUserIdAndType(Long userId, String type);
    
    // Find notifications by user ID and status
    List<Notification> findByUserIdAndStatus(Long userId, String status);
    
    // Find notifications created within a date range
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find recent notifications for a user (last 30 days)
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    // Count notifications by user ID
    long countByUserId(Long userId);
    
    // Count unread notifications by user ID (assuming status 'UNREAD')
    long countByUserIdAndStatus(Long userId, String status);
    
    // Find the latest notification for a user
    Optional<Notification> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Delete old notifications (older than specified date)
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    void deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
}