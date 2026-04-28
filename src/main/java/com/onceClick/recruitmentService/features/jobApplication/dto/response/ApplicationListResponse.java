// dto/response/ApplicationListResponse.java
package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class ApplicationListResponse {
    private UUID applicationId;
    private UUID jobId;
    private String jobTitle;
    private UUID candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String resumeUrl;
    private String status;
    private String statusDisplay;
    private Instant appliedAt;
    private BigDecimal matchScore;
    private Boolean hasInterviewScheduled;
    private BigDecimal candidateExperienceYear;
}