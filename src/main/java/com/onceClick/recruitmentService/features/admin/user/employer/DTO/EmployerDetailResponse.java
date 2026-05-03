package com.onceClick.recruitmentService.features.admin.user.employer.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EmployerDetailResponse {
    private UUID employerId;
    private String email;
    private String phone;
    private String surname;
    private String name;
    private LocalDate birthday;
    private String province;
    private String commune;
    private Boolean gender;
    private String about;
    private String avatarUrl;
    private String backgroundUrl;
    private String referenceLink;
    private String cccd;
    private Instant cccdVerifiedAt;
    private String verificationLevel;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID companyId;
    private String companyName;
    private String companyTaxCode;
}
