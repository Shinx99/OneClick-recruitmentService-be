package com.onceClick.recruitmentService.features.job.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CreateJobResponseDto(

        UUID jobId,
        String title,
        String status,
        UUID companyId,
        Integer applicationCount,
        Integer viewCount

) {}
