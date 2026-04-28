package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

// JobApplicationResponse.java (cho candidate xem mình đã apply job nào)
@Data
@Builder
public class JobApplicationResponse {
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private String status;
    private String statusDisplay;
    private Instant appliedAt;
}