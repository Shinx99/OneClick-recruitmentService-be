package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @EmbeddedId
    private JobApplicationId id;  // ← Dùng class ID mới

    @Column(name = "resume_id")
    private UUID resumeId;

    @Column(name = "status", length = 50)
    private String status = "pending";

    @Column(name = "applied_at", updatable = false)
    private Instant appliedAt = Instant.now();

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    // Helper methods để dễ truy cập composite key
    public UUID getJobId() {
        return id != null ? id.getJobId() : null;
    }

    public UUID getCandidateId() {
        return id != null ? id.getCandidateId() : null;
    }

    public void setJobId(UUID jobId) {
        if (id == null) {
            id = new JobApplicationId();
        }
        id.setJobId(jobId);
    }

    public void setCandidateId(UUID candidateId) {
        if (id == null) {
            id = new JobApplicationId();
        }
        id.setCandidateId(candidateId);
    }
}
