package com.onceClick.recruitmentService.features.job.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GetJobsResponseDto(
        UUID jobId,
        UUID companyId,
        String companyName,
        String companyLogoUrl,
        String title,
        String description,
        String requirement,
        String level,
        String jobType,
        String province,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        BigDecimal experienceMinYear,
        LocalDate applicationDeadline,
        Integer applicationCount,
        Integer viewCount,
        String status,
        String imgUrl,
        Instant createdAt
) {}