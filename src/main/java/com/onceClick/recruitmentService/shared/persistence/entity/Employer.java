package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employer")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employer {

    @Id
    @Column(name = "employer_id", nullable = false)
    private UUID employerId;  // UUID từ Auth Service, không auto-generate

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "surname", nullable = false, length = 50)
    private String surname;

    @Column(name = "about", columnDefinition = "TEXT")
    private String about;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "commune", length = 255)
    private String commune;

    @Column(name = "gender")
    private Boolean gender; // true = nam, false = nữ

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "background_url", columnDefinition = "TEXT")
    private String backgroundUrl;

    @Column(name = "reference_link", columnDefinition = "TEXT")
    private String referenceLink;

    @Column(name = "level", length = 100)
    private String level;

    @Column(name = "experience_year", precision = 4, scale = 1)
    private BigDecimal experienceYear;

    @Column(name = "cccd", length = 20, unique = true)
    private String cccd;

    @Column(name = "cccd_verified_at")
    private Instant cccdVerifiedAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "verification_level", length = 50)
    private String verificationLevel = "lv3";

    @Column(name = "total_job_posted")
    private Integer totalJobPosted = 0;

    @CreationTimestamp
    @Column(name = "consent_data_at", nullable = false)
    private Instant consentDataAt;

    @Column(name = "consent_version", length = 50)
    private String consentVersion = "v1.0";

    @Column(name = "status", length = 50)
    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
