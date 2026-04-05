package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "candidate")
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Candidate implements Persistable<UUID> {

    @Id
    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    //-----------------------------------------------------------------------------
    // For persist issue when syncing data
    @Transient                  // <- Not save into db
    @Getter(AccessLevel.NONE)
    private boolean isNew;      // <- Flag define this is new entity

    @Override
    public UUID getId(){
        return candidateId;
    }

    @Override
    public boolean isNew(){
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew(){
        this.isNew = false;     // <- After save/laod, mark this is not new anymore
    }

    //-----------------------------------------------------------------------------

    @Column(name = "about", columnDefinition = "TEXT")
    private String about;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "surname", length = 50)
    private String surname;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "commune", length = 255)
    private String commune;

    @Column(name = "gender")
    private Boolean gender; // true = nam, false = nữ

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "background_url", columnDefinition = "TEXT")
    private String backgroundUrl;

    @Column(name = "reference_link", columnDefinition = "TEXT")
    private String referenceLink;

    @CreationTimestamp
    @Column(name = "consent_data_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant consentDataAt;

    @Column(name = "consent_version", nullable = false, length = 50)
    private String consentVersion;

    @Column(name = "cccd", length = 20, unique = true)
    private String cccd;

    @Column(name = "cccd_verified_at", columnDefinition = "TIMESTAMPTZ")
    private Instant cccdVerifiedAt;

    @Column(name = "verification_level", length = 50)
    private String verificationLevel = "lv3";

    @Column(name = "status", length = 50)
    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;

}
