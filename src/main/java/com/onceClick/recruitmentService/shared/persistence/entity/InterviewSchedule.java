package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "interview_schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "scheduled_time", nullable = false)
    private Instant scheduledTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 60;

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    @Column(name = "meeting_password", length = 50)
    private String meetingPassword;

    @Column(length = 255)
    private String location;

    @Column(name = "interview_type", length = 50)
    private String interviewType = "TECHNICAL";

    @Column(name = "interviewer_name", length = 255)
    private String interviewerName;

    @Column(name = "interviewer_email", length = 255)
    private String interviewerEmail;

    @Column(length = 50)
    private String status = "SCHEDULED";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(length = 50)
    private String result;

    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    @Column(name = "cancelled_reason", columnDefinition = "TEXT")
    private String cancelledReason;

    @Column(name = "rescheduled_from")
    private UUID rescheduledFrom;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}