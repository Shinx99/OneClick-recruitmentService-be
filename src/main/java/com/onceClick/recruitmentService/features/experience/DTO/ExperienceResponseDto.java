package com.onceClick.recruitmentService.features.experience.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ExperienceResponseDto {
    private UUID experienceId;
    private UUID companyId;
    private String companyName;          // Tên công ty (từ bảng Company hoặc custom)
    private String customCompanyName;    // Giữ lại để khi edit biết nguồn
    private String headline;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String locationType;
    private String employmentLocation;
    private String employmentIndustry;
    private Boolean isCurrent;
}
