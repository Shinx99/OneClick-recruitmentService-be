package com.onceClick.recruitmentService.features.job.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TopJobsResponseDto(
        UUID jobId,
        String title,
        String province,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        Integer viewCount,
        String jobType,

        UUID companyId,
        String companyName,
        String companyLogoUrl


) {
}
