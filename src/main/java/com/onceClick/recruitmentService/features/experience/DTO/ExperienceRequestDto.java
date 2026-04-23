package com.onceClick.recruitmentService.features.experience.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExperienceRequestDto {
    private UUID companyId;              // ID công ty nếu chọn từ danh sách
    private String customCompanyName;    // Tên công ty tự nhập (khi companyId null)
    private String headline;
    private String employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String locationType;
    private String employmentLocation;
    private String employmentIndustry;
    private Boolean isCurrent = false;
}
