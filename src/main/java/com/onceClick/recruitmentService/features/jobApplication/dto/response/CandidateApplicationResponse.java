package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

// CandidateApplicationResponse.java (cho employer xem ai đã apply)
@Data
@Builder
public class CandidateApplicationResponse {
    private UUID candidateId;
    private UUID resumeId;
    private String status;
    private Instant appliedAt;
    private String note;
}