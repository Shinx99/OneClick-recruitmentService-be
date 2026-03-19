package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidate_certificate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCertificate {
    
    @Id
    @GeneratedValue
    @Column(name = "certificate_id", columnDefinition = "UUID")
    private UUID certificateId;
    
    @Column(name = "candidate_id", nullable = false, columnDefinition = "UUID")
    private UUID candidateId;
    
    @Column(name = "certificate_name", nullable = false, length = 255)
    private String certificateName;
    
    @Column(name = "issuing_organization", length = 255)
    private String issuingOrganization;
    
    @Column(name = "issue_date")
    private LocalDate issueDate;
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Column(name = "credential_id", length = 255)
    private String credentialId;
    
    @Column(name = "credential_url", columnDefinition = "TEXT")
    private String credentialUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;
}
