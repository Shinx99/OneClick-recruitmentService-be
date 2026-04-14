package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.GetJobsResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.exception.ResourceNotFoundException;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetRelatedJobsHandler {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<GetJobsResponseDto>> getRelatedJobs(UUID jobId) {

        log.info("Fetching related jobs for jobId: {}", jobId);

        // 1. Lấy Job hiện tại để biết majorPreferred
        Job currentJob = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "jobId", jobId));

        // 2. Tìm TẤT CẢ related jobs theo majorPreferred (ngành)
        String majorPreferred = currentJob.getMajorPreferred();
        List<Job> relatedJobs;

        if (majorPreferred != null && !majorPreferred.isBlank()) {
            // Ưu tiên 1: Tìm tất cả jobs cùng ngành
            relatedJobs = jobRepository.findAllRelatedJobsByMajor(majorPreferred, jobId);
            log.info("Found {} related jobs by major '{}' for jobId: {}", relatedJobs.size(), majorPreferred, jobId);
        } else {
            relatedJobs = Collections.emptyList();
        }

        // 3. Fallback: nếu không có kết quả → lấy tất cả job active (trừ job hiện tại)
        if (relatedJobs.isEmpty()) {
            log.info("No related jobs by major for jobId: {}, falling back to latest active jobs", jobId);
            relatedJobs = jobRepository.findAllFallbackRelatedJobs(jobId);
        }

        // 4. Batch fetch Company (1 query thay vì N)
        List<UUID> companyIds = relatedJobs.stream()
                .map(Job::getCompanyId)
                .distinct()
                .toList();

        Map<UUID, Company> companyMap = companyRepository.findAllByCompanyIdIn(companyIds).stream()
                .collect(Collectors.toMap(Company::getCompanyId, company -> company));

        // 5. Map sang DTO (tái sử dụng GetJobsResponseDto)
        List<GetJobsResponseDto> dtoList = relatedJobs.stream()
                .map(job -> mapToDto(job, companyMap))
                .toList();

        return ApiResponse.success("Lấy danh sách công việc liên quan thành công", dtoList);
    }

    private GetJobsResponseDto mapToDto(Job job, Map<UUID, Company> companyMap) {
        Company company = companyMap.get(job.getCompanyId());

        return new GetJobsResponseDto(
                job.getJobId(),
                job.getCompanyId(),
                company != null ? company.getCompanyName() : null,
                company != null ? company.getLogoUrl() : null,
                job.getTitle(),
                job.getDescription(),
                job.getRequirement(),
                job.getLevel(),
                job.getJobType(),
                job.getProvince(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getExperienceMinYear(),
                job.getApplicationDeadline(),
                job.getApplicationCount(),
                job.getViewCount(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }
}
