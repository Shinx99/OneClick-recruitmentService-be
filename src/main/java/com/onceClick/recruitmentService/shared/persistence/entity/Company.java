package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "company")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @Column(name = "company_id", nullable = false)
    private UUID companyId;  // UUID từ Auth Service, không auto-generate

    @Column(name = "company_name", nullable = false, unique = true, columnDefinition = "TEXT")
    private String companyName;

    @Column(name = "tax_code", nullable = false, unique = true, length = 20)
    private String taxCode;

    @Column(name = "business_license_url", columnDefinition = "TEXT")
    private String businessLicenseUrl;

    @Column(name = "business_rep_name", length = 255)
    private String businessRepName;

    @Column(name = "financial_proof_url", columnDefinition = "TEXT")
    private String financialProofUrl;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "website_url", columnDefinition = "TEXT")
    private String websiteUrl;

    @Column(name = "province_code", length = 10)
    private String provinceCode;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "size_range", nullable = false, length = 50)
    private String sizeRange;

    @Column(name = "overview", nullable = false, columnDefinition = "TEXT")
    private String overview;

    @Column(name = "background_url", columnDefinition = "TEXT")
    private String backgroundUrl;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "verification_level", length = 50)
    private String verificationLevel = "lv3";

    @Column(name = "status", length = 50)
    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}

