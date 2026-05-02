package com.onceClick.recruitmentService.features.employer.dto.response;

import com.onceClick.recruitmentService.shared.persistence.entity.Company;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployerResponseDto {

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

    private String verificationLevel;

    private Integer totalJobPosted;

    private String consentVersion;

    private String status;

}
