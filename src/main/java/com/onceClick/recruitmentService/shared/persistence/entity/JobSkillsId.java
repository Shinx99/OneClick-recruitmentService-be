package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JobSkillsId implements Serializable {

    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "skills_id", nullable = false)
    private UUID skillsId;

}
