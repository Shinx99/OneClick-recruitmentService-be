package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidate_education")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateEducation {
    
    @Id
    @GeneratedValue
    @Column(name = "education_id", columnDefinition = "UUID")
    private UUID educationId;
    
    @Column(name = "candidate_id", nullable = false, columnDefinition = "UUID")
    private UUID candidateId;
    
    @Column(name = "school_name", nullable = false, length = 255)
    private String schoolName;
    
    @Column(name = "degree", length = 100)
    private String degree;
    
    @Column(name = "field_of_study", length = 255)
    private String fieldOfStudy;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "is_current")
    private Boolean isCurrent = false;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
    
    @Column(name = "reference_link", columnDefinition = "TEXT")
    private String referenceLink;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;
}
