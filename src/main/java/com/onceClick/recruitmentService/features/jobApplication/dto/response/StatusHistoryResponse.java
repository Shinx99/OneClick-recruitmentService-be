// dto/response/StatusHistoryResponse.java
package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class StatusHistoryResponse {
    private String oldStatus;
    private String newStatus;
    private String newStatusDisplay;
    private String changedBy;
    private String changedByName;
    private String note;
    private Instant createdAt;
}