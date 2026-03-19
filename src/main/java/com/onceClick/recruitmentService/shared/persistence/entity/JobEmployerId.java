package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
public class JobEmployerId implements Serializable {
    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "employer_id", nullable = false)
    private UUID employerId;
}
