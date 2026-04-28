// dto/response/ApplicationDetailResponse.java
package com.onceClick.recruitmentService.features.jobApplication.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ApplicationDetailResponse {
    private UUID applicationId;
    private UUID jobId;
    private String jobTitle;
    private String jobDescription;
    private String jobRequirement;
    
    private UUID candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String candidateAvatar;
    private String candidateLocation;
    private Integer candidateExperienceYear;
    
    private UUID resumeId;
    private String resumeUrl;
    private String careerGoal;
    private List<String> skills;
    
    private String status;
    private String statusDisplay;
    private String note;
    private Instant appliedAt;
    private Instant updatedAt;
    
    private Integer matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private String matchReason;
    
    private List<StatusHistoryResponse> history;
    private InterviewScheduleResponse upcomingInterview;
}