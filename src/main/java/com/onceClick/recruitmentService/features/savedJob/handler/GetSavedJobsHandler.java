package com.onceClick.recruitmentService.features.savedJob.handler;


import com.onceClick.recruitmentService.features.savedJob.dto.response.SavedJobResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.CompanyRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSavedJobsHandler {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<SavedJobResponseDto>> getSavedJobs(UUID candidateId, Pageable pageable) {
        // 1. Lấy Page chứa cả thực thể SavedJob (thay vì chỉ mỗi UUID)
        Page<com.onceClick.recruitmentService.shared.persistence.entity.SavedJob> savedJobsPage =
                savedJobRepository.findByCandidateId(candidateId, pageable);

        List<com.onceClick.recruitmentService.shared.persistence.entity.SavedJob> savedJobEntities = savedJobsPage.getContent();

        if (savedJobEntities.isEmpty()) {
            return ApiResponse.success("Danh sách trống", PageResponse.from(new PageImpl<>(Collections.emptyList(), pageable, 0)));
        }

        // 2. Gom danh sách JobId từ các thực thể đã lưu
        List<UUID> jobIds = savedJobEntities.stream()
                .map(sj -> sj.getId().getJobId())
                .toList();

        // 3. Lấy thông tin Job và Map lại (giữ nguyên logic cũ của bạn)
        List<Job> jobs = jobRepository.findAllById(jobIds);
        Map<UUID, Job> jobMap = jobs.stream()
                .collect(Collectors.toMap(Job::getJobId, j -> j));

        // 4. Lấy thông tin Company và Map lại
        List<UUID> companyIds = jobs.stream().map(Job::getCompanyId).distinct().toList();
        Map<UUID, Company> companyMap = companyRepository.findAllByCompanyIdIn(companyIds)
                .stream().collect(Collectors.toMap(Company::getCompanyId, c -> c));

        // 5. Mapping sang DTO với thời gian savedAt thực tế
        List<SavedJobResponseDto> dtos = savedJobEntities.stream()
                .map(savedJobEntity -> {
                    UUID jobId = savedJobEntity.getId().getJobId();
                    Job job = jobMap.get(jobId);
                    if (job == null) return null;

                    Company company = companyMap.get(job.getCompanyId());

                    return new SavedJobResponseDto(
                            job.getJobId(),
                            job.getTitle(),
                            job.getImgUrl(),
                            job.getLevel(),
                            job.getJobType(),
                            job.getProvince(),
                            job.getSalaryMin(),
                            job.getSalaryMax(),
                            job.getApplicationDeadline(),
                            job.getStatus(),
                            company != null ? company.getCompanyId() : null,
                            company != null ? company.getCompanyName() : null,
                            company != null ? company.getLogoUrl() : null,
                            // FIX TẠI ĐÂY: Lấy savedAt từ thực thể SavedJob
                            savedJobEntity.getSavedAt()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        Page<SavedJobResponseDto> resultPage = new PageImpl<>(dtos, pageable, savedJobsPage.getTotalElements());

        return ApiResponse.success("Lấy danh sách việc đã lưu thành công", PageResponse.from(resultPage));
    }


}
