package com.onceClick.recruitmentService.features.resume.dto.request;

import com.onceClick.recruitmentService.features.scanCv_Profile.dto.parsedDataDtos.ParsedData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeRequest {

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

}
