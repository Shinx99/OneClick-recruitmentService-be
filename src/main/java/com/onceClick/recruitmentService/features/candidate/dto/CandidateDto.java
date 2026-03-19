package com.onceClick.recruitmentService.features.candidate.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class CandidateDto {

    private UUID candidateId;

    private String about;

    private String surname;

    private String name;

    private LocalDate birthday;

    private String province;

    private String commune;

    private Boolean gender; // true = nam, false = nữ

    private String avatarUrl;

    private String backgroundUrl;

    private String referenceLink;

    private Instant consentDataAt;

    private String consentVersion;

    private String cccd;

    private Instant cccdVerifiedAt;

    private String verificationLevel;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;

}
