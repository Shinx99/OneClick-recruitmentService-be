package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;
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
public class Employer implements Persistable<UUID> {

    @Id
    @Column(name = "employer_id", nullable = false)
    private UUID employerId;  // UUID từ Auth Service, không auto-generate

    //-----------------------------------------------------------------------------
    // For persist issue when syncing data
    @Transient
    @Getter(AccessLevel.NONE)
    private boolean isNew;

    @Override
    public UUID getId(){
        return employerId;
    }

    @Override
    public boolean isNew(){
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew(){
        this.isNew = false;
    }

    //-----------------------------------------------------------------------------

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "company_id", nullable = false)
//    private Company company;

    // để sau này mới tạo company và gắn vào hoặc nullable = true
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;


    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "surname", length = 50)
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
    private String verificationLevel;

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
