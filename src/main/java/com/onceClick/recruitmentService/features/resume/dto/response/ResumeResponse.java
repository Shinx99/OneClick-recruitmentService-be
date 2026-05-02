package com.onceClick.recruitmentService.features.resume.dto.response;

import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumeResponse {

    private UUID resumeId;

    private UUID candidateId;

    private Boolean isDefault;

    private String careerGoal;

    private String major;

    private BigDecimal experienceYear;

    private String salaryExpectation;

    private String resumeUploadUrl;

    private String imgUrl;

    private Integer viewCount;

    private String status;

    private Boolean findJob;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    private ParsedData parsedData;

    // Candidate
    private String surname;

    private String name;

    private String email;

}
