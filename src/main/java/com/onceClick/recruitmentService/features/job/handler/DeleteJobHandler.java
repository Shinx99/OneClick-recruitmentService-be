package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ForbiddenException;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.entity.JobEmployer;
import com.onceClick.recruitmentService.shared.persistence.entity.JobEmployerId;
import com.onceClick.recruitmentService.shared.persistence.repository.JobEmployerRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteJobHandler {

    private final JobRepository jobRepository;
    private final JobEmployerRepository jobEmployerRepository;

    @CacheEvict(value = {
            "jobs",
            "job:by-employer-id",
            "job:by-company-id",
            "job:by-job-id"
    }, allEntries = true)
    @Transactional
    public ApiResponse<Void> deleteJob(UUID jobId, UUID employerId) {

        // 1. Tìm job theo ID
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        // 2. Kiểm tra quyền: employer phải là owner của job
        JobEmployerId jobEmployerIdKey = JobEmployerId.builder()
                .jobId(jobId)
                .employerId(employerId)
                .build();

        JobEmployer jobEmployer = jobEmployerRepository.findById(jobEmployerIdKey)
                .orElseThrow(() -> new ForbiddenException("job", "delete"));

        if (!"owner".equalsIgnoreCase(jobEmployer.getAccessRole())) {
            throw new ForbiddenException("Only job owner can delete this job");
        }

        // 3. Soft delete: đổi status thành "deleted"
        job.setStatus("deleted");
        job.setUpdatedBy(employerId);
        jobRepository.save(job);

        return ApiResponse.success("Job deleted successfully!", null);
    }
}
