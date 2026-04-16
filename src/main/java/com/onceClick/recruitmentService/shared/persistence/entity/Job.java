package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "job")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job extends BaseEntity {
    
    @Id
    @GeneratedValue
    @Column(name = "job_id")
    private UUID jobId;
    
    @Column(name = "company_id", nullable = false)
    @NotNull
    private UUID companyId;
    
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "requirement", columnDefinition = "TEXT")
    private String requirement;
    
    @Column(name = "major_preffered", length = 255)
    private String majorPreferred;
    
    @Column(name = "level", length = 100)
    private String level;
    
    @Column(name = "job_type", length = 100)
    private String jobType;
    
    @Column(name = "province", length = 100)
    private String province;
    
    @Column(name = "commune", length = 255)
    private String commune;
    
    @Column(name = "salary_min", precision = 12, scale = 2)
    private BigDecimal salaryMin;
    
    @Column(name = "salary_max", precision = 12, scale = 2)
    private BigDecimal salaryMax;
    
    @Column(name = "experience_min_year", precision = 4, scale = 1)
    private BigDecimal experienceMinYear;
    
    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;
    
    @Column(name = "application_count", nullable = false)
    @Min(0)
    private Integer applicationCount = 0;
    
    @Column(name = "view_count", nullable = false)
    @Min(0)
    private Integer viewCount = 0;
    
    @Column(name = "status", length = 50, nullable = false)
    @NotNull
    private String status = "active";
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    @Column(name = "updated_by")
    private UUID updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;

}
