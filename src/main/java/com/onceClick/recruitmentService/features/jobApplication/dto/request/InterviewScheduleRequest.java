// dto/request/InterviewScheduleRequest.java
package com.onceClick.recruitmentService.features.jobApplication.dto.request;

import lombok.Data;
import java.time.Instant;

@Data
public class InterviewScheduleRequest {
    private Instant scheduledTime;
    private Integer durationMinutes;
    private String meetingLink;
    private String meetingPassword;
    private String location;
    private String interviewType;
    private String interviewerName;
    private String interviewerEmail;
    private String notes;
}