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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_statistic_daily")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobStatisticDaily {
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "job_id", nullable = false)
    private UUID jobId;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Column(name = "apply_count", nullable = false)
    private Integer applyCount = 0;
    
    @Column(name = "save_count", nullable = false)
    private Integer saveCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

}
