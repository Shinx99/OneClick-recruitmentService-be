package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_employer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobEmployer {
    
   /* @Id
    @Column(name = "job_id", nullable = false, columnDefinition = "UUID")
    private UUID jobId;
    
    @Id
    @Column(name = "employer_id", nullable = false, columnDefinition = "UUID")
    private UUID employerId;*/

    @EmbeddedId
    private JobEmployerId id;

    @Column(name = "access_role", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'editor'")
    private String accessRole = "editor";
    
    @Column(name = "granted_at",nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant grantedAt;
}
