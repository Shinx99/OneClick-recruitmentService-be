// dto/request/UpdateStatusRequest.java
package com.onceClick.recruitmentService.features.jobApplication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotBlank
    @Pattern(regexp = "^(pending|reviewed|interview|accepted|rejected)$")
    private String status;

    private String note;

    private InterviewScheduleRequest interviewSchedule;
}