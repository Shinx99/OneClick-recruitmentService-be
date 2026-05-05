package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.GetJobsResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
public class GetEmployerJobsHandler {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    // GetEmployerJobsHandler.java
    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<GetJobsResponseDto>> getEmployerJobs(
            UUID employerId,
            String keyword,
            String status,
            Pageable pageable) {

        log.info("Fetching jobs for employer - employerId: {}, keyword: {}, status: {}",
                employerId, keyword, status);

        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        // Lấy danh sách jobs
        List<Job> jobs = jobRepository.findByEmployerIdAndFiltersNative(
                employerId, keyword, status, limit, offset);

        // Lấy tổng số
        long total = jobRepository.countByEmployerIdAndFilters(employerId, keyword, status);

        // Batch fetch company data
        List<UUID> companyIds = jobs.stream()
                .map(Job::getCompanyId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        final Map<UUID, Company> companyMap;
        if (!companyIds.isEmpty()) {
            companyMap = companyRepository.findAllByCompanyIdIn(companyIds).stream()
                    .collect(Collectors.toMap(Company::getCompanyId, company -> company));
        } else {
            companyMap = Collections.emptyMap();
        }

        // Map sang DTO
        List<GetJobsResponseDto> dtoList = jobs.stream()
                .map(job -> {
                    Company company = companyMap.get(job.getCompanyId());
                    return mapToDto(job, company);
                })
                .collect(Collectors.toList());

        // Tạo Page response
        Page<GetJobsResponseDto> dtoPage = new PageImpl<>(dtoList, pageable, total);

        return ApiResponse.success("Lấy danh sách công việc thành công", PageResponse.from(dtoPage));
    }

    private GetJobsResponseDto mapToDto(Job job, Company company) {
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
                job.getImgUrl(),
                job.getCreatedAt()
        );
    }
}