package com.onceClick.recruitmentService.features.jobApplication.dto.request;

import lombok.Data;

@Data
public class UpdateStatusRequest {
    private String status;
    private String note;
}