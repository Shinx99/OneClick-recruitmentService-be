package com.onceClick.recruitmentService.features.job.handler;

import com.onceClick.recruitmentService.features.job.dto.response.GetJobsResponseDto;
import com.onceClick.recruitmentService.shared.dto.ApiResponse;
import com.onceClick.recruitmentService.shared.dto.PageResponse;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;
import com.onceClick.recruitmentService.shared.persistence.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetJobsHandler {

    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public ApiResponse<PageResponse<GetJobsResponseDto>> getAllJobs(
            String keyword,
            String province,
            String level,
            Pageable pageable) {

        log.info("Fetching jobs with keyword: {}, province: {}, level: {}", keyword, province, level);

        // 1. Call Repo get Page<Job> instead of List<Job>
        Page<Job> jobPage = jobRepository.searchAndFilterJobs(keyword, province, level, pageable);

        // 2. Map Page<Job> to Page<GetJobsResponseDto>
        Page<GetJobsResponseDto> dtoPage = jobPage.map(this::mapToDto);

        // 3. Đóng gói vào PageResponse
        PageResponse<GetJobsResponseDto> pageResponse = PageResponse.from(dtoPage);

        // 4. Trả về ApiResponse
        return ApiResponse.success("Fetched jobs successfully!", pageResponse);
    }

    private GetJobsResponseDto mapToDto(Job job) {
        return new GetJobsResponseDto(
                job.getJobId(),
                job.getCompanyId(),
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