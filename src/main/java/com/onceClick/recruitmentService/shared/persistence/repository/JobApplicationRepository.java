package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {  // ← Đổi thành UUID

    // ========== CÁC METHOD CƠ BẢN ==========

    // Tìm theo jobId
    List<JobApplication> findByJobId(UUID jobId);

    // Tìm theo candidateId
    List<JobApplication> findByCandidateId(UUID candidateId);

    // Tìm theo applicationId (có sẵn từ JpaRepository)
    Optional<JobApplication> findByApplicationId(UUID applicationId);

    // Tìm theo jobId và candidateId
    Optional<JobApplication> findByJobIdAndCandidateId(UUID jobId, UUID candidateId);

    // Kiểm tra đã ứng tuyển chưa
    boolean existsByJobIdAndCandidateId(UUID jobId, UUID candidateId);

    // Tìm theo jobId và status
    List<JobApplication> findByJobIdAndStatus(UUID jobId, String status);

    // ========== PHÂN TRANG & FILTER ==========

    // Phân trang theo jobId
    Page<JobApplication> findByJobId(UUID jobId, Pageable pageable);

    // Phân trang theo jobId và status
    Page<JobApplication> findByJobIdAndStatus(UUID jobId, String status, Pageable pageable);

    // Phân trang theo candidateId
    Page<JobApplication> findByCandidateId(UUID candidateId, Pageable pageable);

    // ========== THỐNG KÊ ==========

    @Query("SELECT ja.status, COUNT(ja) FROM JobApplication ja WHERE ja.jobId = :jobId GROUP BY ja.status")
    List<Object[]> countByStatusForJob(@Param("jobId") UUID jobId);

    @Query("SELECT ja.status, COUNT(ja) FROM JobApplication ja WHERE ja.candidateId = :candidateId GROUP BY ja.status")
    List<Object[]> countByStatusForCandidate(@Param("candidateId") UUID candidateId);

    @Query("SELECT FUNCTION('DATE_TRUNC', 'month', ja.appliedAt), COUNT(ja) FROM JobApplication ja GROUP BY FUNCTION('DATE_TRUNC', 'month', ja.appliedAt) ORDER BY FUNCTION('DATE_TRUNC', 'month', ja.appliedAt) DESC")
    List<Object[]> countByMonth();

    @Query("SELECT DATE(ja.appliedAt), COUNT(ja) FROM JobApplication ja WHERE ja.jobId = :jobId GROUP BY DATE(ja.appliedAt) ORDER BY DATE(ja.appliedAt) DESC")
    List<Object[]> countByDayForJob(@Param("jobId") UUID jobId, Pageable pageable);

    // ========== CẬP NHẬT HÀNG LOẠT ==========

    @Modifying
    @Transactional
    @Query("UPDATE JobApplication ja SET ja.status = :status, ja.updatedAt = CURRENT_TIMESTAMP WHERE ja.jobId = :jobId AND ja.status = :oldStatus")
    int updateStatusByJobId(@Param("jobId") UUID jobId, @Param("status") String status, @Param("oldStatus") String oldStatus);

    @Modifying
    @Transactional
    @Query("DELETE FROM JobApplication ja WHERE ja.jobId = :jobId AND ja.status = 'pending'")
    int deletePendingApplicationsByJobId(@Param("jobId") UUID jobId);

    // ========== LỌC THEO THỜI GIAN ==========

    List<JobApplication> findByAppliedAtBetween(Instant startDate, Instant endDate);

    List<JobApplication> findByJobIdAndAppliedAtBetween(UUID jobId, Instant startDate, Instant endDate);

    // ========== LẤY ID ĐỂ TỐI ƯU ==========

    @Query("SELECT ja.candidateId FROM JobApplication ja WHERE ja.jobId = :jobId")
    List<UUID> findCandidateIdsByJobId(@Param("jobId") UUID jobId);

    @Query("SELECT ja.jobId FROM JobApplication ja WHERE ja.candidateId = :candidateId")
    List<UUID> findJobIdsByCandidateId(@Param("candidateId") UUID candidateId);

    @Query("SELECT COUNT(ja) > 0 FROM JobApplication ja " +
            "WHERE ja.candidateId = :candidateId " +
            "AND ja.jobId IN (SELECT j.jobId FROM Job j WHERE j.createdBy = :employerId)")
    boolean existsByCandidateIdAndEmployerId(@Param("candidateId") UUID candidateId,
                                             @Param("employerId") UUID employerId);
}