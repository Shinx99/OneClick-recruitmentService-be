package com.onceClick.recruitmentService.features.savedJob.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SavedJobResponseDto(
        UUID jobId,
        String title,
        String imgUrl,
        String level,
        String jobType,
        String province,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        LocalDate applicationDeadline,
        String status,

        UUID companyId,
        String companyName,
        String companyLogoUrl,

        Instant savedAt   // Thời điểm candidate lưu job này
) {}
