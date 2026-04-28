// dto/response/InterviewScheduleResponse.java
package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class InterviewScheduleResponse {
    private UUID id;
    private Instant scheduledTime;
    private Integer durationMinutes;
    private String meetingLink;
    private String meetingPassword;
    private String location;
    private String interviewType;
    private String interviewerName;
    private String interviewerEmail;
    private String status;
    private String notes;
}