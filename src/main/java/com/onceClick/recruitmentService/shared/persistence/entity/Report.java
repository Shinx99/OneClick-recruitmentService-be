package com.onceClick.recruitmentService.shared.persistence.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    
    @Id
    @GeneratedValue
    @Column(name = "report_id")
    private UUID reportId;
    
    @Column(name = "type", length = 50, nullable = false)
    private String type;
    
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status = "active";

    @Type(JsonBinaryType.class)
    @Column(name = "data", columnDefinition = "jsonb")
    private JsonNode data;
    
    @Column(name = "candidate_id")
    private UUID candidateId;
    
    @Column(name = "job_id")
    private UUID jobId;
    
    @Column(name = "employer_id")
    private UUID employerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;
}
