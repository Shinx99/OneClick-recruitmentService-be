// dto/response/ApplicationStatsResponse.java
package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationStatsResponse {
    private long total;
    private long pending;
    private long reviewed;
    private long interview;
    private long accepted;
    private long rejected;
    
    private double pendingRate;
    private double reviewedRate;
    private double interviewRate;
    private double acceptedRate;
    private double rejectedRate;
}