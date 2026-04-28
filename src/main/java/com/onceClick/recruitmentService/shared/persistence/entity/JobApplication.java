package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id")
    private UUID applicationId;

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "resume_id")
    private UUID resumeId;

    @Column(name = "status", length = 50)
    private String status = "pending";

    @Column(name = "applied_at", updatable = false)
    private Instant appliedAt = Instant.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "match_score", precision = 5, scale = 2)
    private BigDecimal matchScore;
}