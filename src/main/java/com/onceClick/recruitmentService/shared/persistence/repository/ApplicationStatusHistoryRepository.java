// =========================================================
// 1. ApplicationStatusHistoryRepository.java
// =========================================================
package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.ApplicationStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, UUID> {

    /**
     * Lấy lịch sử theo application_id (sắp xếp theo thời gian tăng dần)
     */
    List<ApplicationStatusHistory> findByApplicationIdOrderByCreatedAtAsc(UUID applicationId);
    
    /**
     * Lấy lịch sử theo application_id (có phân trang)
     */
    Page<ApplicationStatusHistory> findByApplicationId(UUID applicationId, Pageable pageable);
    
    /**
     * Lấy lịch sử thay đổi bởi ai đó
     */
    List<ApplicationStatusHistory> findByChangedBy(UUID changedBy);
    
    /**
     * Lấy lịch sử trong khoảng thời gian
     */
    List<ApplicationStatusHistory> findByCreatedAtBetween(Instant start, Instant end);
    
    /**
     * Đếm số lần thay đổi status của một application
     */
    long countByApplicationId(UUID applicationId);
    
    /**
     * Lấy lần thay đổi gần nhất của application
     */
    @Query("SELECT h FROM ApplicationStatusHistory h WHERE h.applicationId = :applicationId ORDER BY h.createdAt DESC")
    List<ApplicationStatusHistory> findLatestByApplicationId(@Param("applicationId") UUID applicationId, Pageable pageable);
    
    /**
     * Thống kê số lượng thay đổi theo status mới
     */
    @Query("SELECT h.newStatus, COUNT(h) FROM ApplicationStatusHistory h GROUP BY h.newStatus")
    List<Object[]> countByNewStatus();
}