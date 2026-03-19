package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "experience")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Experience {

    @Id
    @GeneratedValue
    @Column(name = "experience_id")
    private UUID experienceId;

    @Column(name = "company_id", nullable = false)
    @NotNull
    private UUID companyId;

    @Column(name = "headline", length = 255)
    private String headline;

    @Column(name = "employment_type", length = 100)
    private String employmentType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location_type", length = 100)
    private String locationType;

    @Column(name = "employment_location", length = 255)
    private String employmentLocation;

    @Column(name = "employment_industry", length = 100)
    private String employmentIndustry;

    @Column(name = "is_current")
    private Boolean isCurrent = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;
}
