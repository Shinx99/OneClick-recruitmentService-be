package com.onceClick.recruitmentService.features.employer.dto.request;

import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerRequestDto {

    private UUID employerId;

    private Company company;

    private String name;

    private String email;

    private String phone;

    private String surname;

    private String about;

    private LocalDate birthday;

    private String province;

    private String commune;

    private Boolean gender;

    private String industry;

    private String avatarUrl;

    private String backgroundUrl;

    private String referenceLink;

    private String level;

    private BigDecimal experienceYear;

    private String cccd;

    private Instant cccdVerifiedAt;

    private String verificationLevel;

    private Integer totalJobPosted;

    private String consentVersion;

    private String status;
}
