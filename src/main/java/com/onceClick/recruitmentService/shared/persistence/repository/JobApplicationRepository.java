package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.JobApplication;
import com.onceClick.recruitmentService.shared.persistence.entity.JobApplicationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, JobApplicationId> {
    List<JobApplication> findByIdJobId(UUID jobId);
    List<JobApplication> findByIdCandidateId(UUID candidateId);
    Optional<JobApplication> findByIdJobIdAndIdCandidateId(UUID jobId, UUID candidateId);
    List<JobApplication> findByIdJobIdAndStatus(UUID jobId, String status);

    // Lấy tất cả ứng tuyển của 1 job (cho employer xem ai đã apply)
    @Query("SELECT ja FROM JobApplication ja WHERE ja.id.jobId = :jobId")
    List<JobApplication> findByJobId(@Param("jobId") UUID jobId);

    // Lấy tất cả ứng tuyển của 1 candidate (xem mình đã apply job nào)
    @Query("SELECT ja FROM JobApplication ja WHERE ja.id.candidateId = :candidateId")
    List<JobApplication> findByCandidateId(@Param("candidateId") UUID candidateId);

    // Kiểm tra đã apply chưa
    @Query("SELECT COUNT(ja) > 0 FROM JobApplication ja WHERE ja.id.jobId = :jobId AND ja.id.candidateId = :candidateId")
    boolean existsByJobIdAndCandidateId(@Param("jobId") UUID jobId, @Param("candidateId") UUID candidateId);

    // Tìm 1 ứng tuyển cụ thể
    @Query("SELECT ja FROM JobApplication ja WHERE ja.id.jobId = :jobId AND ja.id.candidateId = :candidateId")
    Optional<JobApplication> findByJobIdAndCandidateId(@Param("jobId") UUID jobId, @Param("candidateId") UUID candidateId);
}