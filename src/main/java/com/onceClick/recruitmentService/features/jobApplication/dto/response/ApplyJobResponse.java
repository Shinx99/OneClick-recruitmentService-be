package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ApplyJobResponse {
    private UUID jobId;
    private UUID candidateId;
    private String status;
    private Instant appliedAt;
}
