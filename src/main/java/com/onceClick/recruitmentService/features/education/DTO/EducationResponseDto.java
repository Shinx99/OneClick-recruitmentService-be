package com.onceClick.recruitmentService.features.education.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EducationResponseDto {
    private UUID educationId;
    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String description;
    private String imageUrl;
    private String referenceLink;
}
