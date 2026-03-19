package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.JobApplication;
import com.onceClick.recruitmentService.shared.persistence.entity.JobApplicationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, JobApplicationId> {
    List<JobApplication> findByIdJobId(UUID jobId);
    List<JobApplication> findByIdCandidateId(UUID candidateId);
    Optional<JobApplication> findByIdJobIdAndIdCandidateId(UUID jobId, UUID candidateId);
    List<JobApplication> findByIdJobIdAndStatus(UUID jobId, String status);
}