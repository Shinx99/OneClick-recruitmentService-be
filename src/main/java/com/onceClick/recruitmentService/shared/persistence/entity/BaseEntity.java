package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jdk.jfr.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Date;


public abstract class BaseEntity {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant updatedAt;




}
