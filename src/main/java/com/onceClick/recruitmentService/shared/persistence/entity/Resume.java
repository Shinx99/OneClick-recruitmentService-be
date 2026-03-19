package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resume")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    
    @Id
    @GeneratedValue
    @Column(name = "resume_id")
    private UUID resumeId;
    
    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;
    
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
}
