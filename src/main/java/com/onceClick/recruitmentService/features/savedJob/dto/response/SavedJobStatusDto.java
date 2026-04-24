package com.onceClick.recruitmentService.features.savedJob.dto.response;

import java.util.UUID;

public record SavedJobStatusDto(
        UUID jobId,
        boolean isSaved
) {}
