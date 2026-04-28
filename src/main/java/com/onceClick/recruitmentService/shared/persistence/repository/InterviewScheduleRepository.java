// =========================================================
// 3. InterviewScheduleRepository.java
// =========================================================
package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.InterviewSchedule;
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
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, UUID> {

    // ========== QUERY CƠ BẢN ==========
    
    /**
     * Lấy tất cả lịch phỏng vấn của một application
     */
    List<InterviewSchedule> findByApplicationIdOrderByScheduledTimeAsc(UUID applicationId);
    
    /**
     * Lấy lịch phỏng vấn sắp tới của application
     */
    @Query("SELECT i FROM InterviewSchedule i WHERE i.applicationId = :applicationId AND i.status = 'SCHEDULED' AND i.scheduledTime > CURRENT_TIMESTAMP ORDER BY i.scheduledTime ASC")
    List<InterviewSchedule> findUpcomingByApplicationId(@Param("applicationId") UUID applicationId);
    
    /**
     * Lấy lịch phỏng vấn theo status
     */
    List<InterviewSchedule> findByStatus(String status);
    
    /**
     * Lấy lịch phỏng vấn trong khoảng thời gian
     */
    List<InterviewSchedule> findByScheduledTimeBetween(Instant start, Instant end);
    
    /**
     * Lấy lịch phỏng vấn của interviewer
     */
    List<InterviewSchedule> findByInterviewerEmail(String email);
    
    // ========== PAGINATION ==========
    
    /**
     * Lấy lịch phỏng vấn của application có phân trang
     */
    Page<InterviewSchedule> findByApplicationId(UUID applicationId, Pageable pageable);
    
    /**
     * Lấy lịch phỏng vấn sắp tới của tất cả application của một candidate
     */
    @Query("SELECT i FROM InterviewSchedule i WHERE i.applicationId IN :applicationIds AND i.status = 'SCHEDULED' AND i.scheduledTime > CURRENT_TIMESTAMP ORDER BY i.scheduledTime ASC")
    List<InterviewSchedule> findUpcomingByApplicationIds(@Param("applicationIds") List<UUID> applicationIds);
    
    // ========== UPDATE ==========
    
    /**
     * Cập nhật trạng thái lịch phỏng vấn
     */
    @Modifying
    @Transactional
    @Query("UPDATE InterviewSchedule i SET i.status = :status, i.updatedAt = CURRENT_TIMESTAMP WHERE i.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") String status);
    
    /**
     * Cập nhật kết quả phỏng vấn
     */
    @Modifying
    @Transactional
    @Query("UPDATE InterviewSchedule i SET i.result = :result, i.feedback = :feedback, i.updatedAt = CURRENT_TIMESTAMP WHERE i.id = :id")
    void updateResult(@Param("id") UUID id, @Param("result") String result, @Param("feedback") String feedback);
    
    /**
     * Hủy tất cả lịch phỏng vấn của một application
     */
    @Modifying
    @Transactional
    @Query("UPDATE InterviewSchedule i SET i.status = 'CANCELLED', i.cancelledReason = :reason, i.updatedAt = CURRENT_TIMESTAMP WHERE i.applicationId = :applicationId AND i.status = 'SCHEDULED'")
    void cancelAllByApplicationId(@Param("applicationId") UUID applicationId, @Param("reason") String reason);
    
    // ========== STATS ==========
    
    /**
     * Đếm số lượng lịch phỏng vấn theo status
     */
    @Query("SELECT i.status, COUNT(i) FROM InterviewSchedule i GROUP BY i.status")
    List<Object[]> countByStatus();
    
    /**
     * Đếm số lượng lịch phỏng vấn của application theo status
     */
    @Query("SELECT i.status, COUNT(i) FROM InterviewSchedule i WHERE i.applicationId = :applicationId GROUP BY i.status")
    List<Object[]> countByStatusForApplication(@Param("applicationId") UUID applicationId);
    
    /**
     * Lấy lịch phỏng vấn cần nhắc (trong vòng 1 giờ tới)
     */
    @Query("SELECT i FROM InterviewSchedule i WHERE i.status = 'SCHEDULED' AND i.scheduledTime BETWEEN :now AND :oneHourLater")
    List<InterviewSchedule> findInterviewsNeedReminder(@Param("now") Instant now, @Param("oneHourLater") Instant oneHourLater);
}