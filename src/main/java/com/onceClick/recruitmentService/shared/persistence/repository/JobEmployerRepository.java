package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.JobEmployer;
import com.onceClick.recruitmentService.shared.persistence.entity.JobEmployerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobEmployerRepository
        extends JpaRepository<JobEmployer, JobEmployerId> {
    List<JobEmployer> findByIdJobId(UUID jobId);
    List<JobEmployer> findByIdEmployerId(UUID employerId);
    boolean existsByIdJobIdAndIdEmployerId(UUID jobId, UUID employerId);
}