package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.GetJobsResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetJobsHandler {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<GetJobsResponseDto>> getAllJobs(
            String keyword,
            String province,
            String level,
            String jobType,
            BigDecimal salaryMin,
            BigDecimal salaryMax,
            BigDecimal experienceMax,
            Pageable pageable) {

        log.info("Fetching jobs with keyword: {}, province: {}, level: {}, jobType: {}, salaryMin: {}, salaryMax: {}, experienceMax: {}",
                keyword, province, level, jobType, salaryMin, salaryMax, experienceMax);

        // 1. Build Specification from filters
        Specification<Job> spec = Specification.where(JobSpecification.isActive())
                .and(JobSpecification.hasKeyword(keyword))
                .and(JobSpecification.hasProvince(province))
                .and(JobSpecification.hasLevel(level))
                .and(JobSpecification.hasJobType(jobType))
                .and(JobSpecification.hasSalaryMin(salaryMin))
                .and(JobSpecification.hasSalaryMax(salaryMax))
                .and(JobSpecification.hasExperienceMax(experienceMax));

        // 2. Query with Specification + Pagination
        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

        // 3. Batch fetch company data (1 query instead of N)
        List<UUID> companyIds = jobPage.getContent().stream()
                .map(Job::getCompanyId)
                .distinct()
                .toList();

        Map<UUID, Company> companyMap = companyRepository.findAllByCompanyIdIn(companyIds).stream()
                .collect(Collectors.toMap(Company::getCompanyId, company -> company));

        // 4. Map Page<Job> to Page<GetJobsResponseDto> with company data
        Page<GetJobsResponseDto> dtoPage = jobPage.map(job -> mapToDto(job, companyMap));

        // 5. Wrap in PageResponse
        PageResponse<GetJobsResponseDto> pageResponse = PageResponse.from(dtoPage);

        // 6. Return ApiResponse
        return ApiResponse.success("Fetched jobs successfully!", pageResponse);
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