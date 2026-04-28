// =========================================================
// 2. NotificationRepository.java
// =========================================================
package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // ========== QUERY CƠ BẢN ==========
    
    /**
     * Lấy thông báo theo user_id (phân trang, mới nhất trước)
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Lấy thông báo chưa đọc của user
     */
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId);
    
    /**
     * Đếm số lượng thông báo chưa đọc của user
     */
    long countByUserIdAndIsReadFalse(UUID userId);
    
    /**
     * Lấy thông báo theo loại
     */
    List<Notification> findByUserIdAndType(UUID userId, String type);
    
    /**
     * Lấy thông báo liên quan đến application
     */
    List<Notification> findByRelatedApplicationId(UUID applicationId);
    
    // ========== UPDATE ==========
    
    /**
     * Đánh dấu một thông báo đã đọc
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsRead(@Param("id") UUID id);
    
    /**
     * Đánh dấu tất cả thông báo của user đã đọc
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") UUID userId);
    
    /**
     * Đánh dấu nhiều thông báo đã đọc
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id IN :ids")
    void markMultipleAsRead(@Param("ids") List<UUID> ids);
    
    // ========== DELETE ==========
    
    /**
     * Xóa thông báo cũ hơn ngày chỉ định
     */
    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(Instant date);
    
    /**
     * Xóa tất cả thông báo của user
     */
    @Modifying
    @Transactional
    void deleteByUserId(UUID userId);
    
    // ========== STATS ==========
    
    /**
     * Thống kê số lượng thông báo theo loại
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.userId = :userId GROUP BY n.type")
    List<Object[]> countByTypeForUser(@Param("userId") UUID userId);
    
    /**
     * Thống kê số lượng thông báo theo ngày
     */
    @Query("SELECT DATE(n.createdAt), COUNT(n) FROM Notification n WHERE n.userId = :userId GROUP BY DATE(n.createdAt) ORDER BY DATE(n.createdAt) DESC")
    List<Object[]> countByDayForUser(@Param("userId") UUID userId, Pageable pageable);
}