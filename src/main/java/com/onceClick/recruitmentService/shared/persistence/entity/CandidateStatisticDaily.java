package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "candidate_statistic_daily")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateStatisticDaily {
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "candidate_id", nullable = false)
    private UUID candidateId;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "profile_view_count", nullable = false)
    private Integer profileViewCount = 0;
    
    @Column(name = "apply_count", nullable = false)
    private Integer applyCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

}
