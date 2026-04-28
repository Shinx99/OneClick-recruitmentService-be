// dto/response/EmployerJobResponse.java
package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class EmployerJobResponse {
    private UUID jobId;
    private String title;
    private String description;
    private String status;
    private Integer applicationCount;
    private Integer viewCount;
    private Instant createdAt;
    private Instant updatedAt;
}