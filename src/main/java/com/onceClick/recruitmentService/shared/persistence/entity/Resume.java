package com.onceClick.recruitmentService.shared.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "resume")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    
    @Id
    @GeneratedValue
    @Column(name = "resume_id")
    private UUID resumeId;
    
    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "career_goal", columnDefinition = "TEXT")
    private String careerGoal;
    
    @Column(name = "major", length = 255)
    private String major;
    
    @Column(name = "experience_year", precision = 4, scale = 1)
    private BigDecimal experienceYear;
    
    @Column(name = "salary_expectation", length = 100)
    private String salaryExpectation;
    
    @Column(name = "resume_upload_url")
    private String resumeUploadUrl;
    
    @Column(name = "img_url")
    private String imgUrl;
    
    @Column(name = "view_count", nullable = false)
    @Min(0)
    private Integer viewCount = 0;
    
    @Column(name = "status", length = 50, nullable = false)
    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Type(JsonType.class)  // Hibernate 6+
    @Column(name = "parsed_data", columnDefinition = "JSONB")
    private Map<String, Object> parsedData;  // Hoặc JsonNode, ObjectNode

    public static ResumeBuilder builder() {
        return new ResumeBuilder();
    }
    public void softDelete() {
        this.status = "deleted";
        this.deletedAt = Instant.now();
    }
}
