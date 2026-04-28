package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "type", length = 50, nullable = false)
    private String type;  // NEW_APPLICATION, STATUS_CHANGED, INTERVIEW_SCHEDULED

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "related_application_id")
    private UUID relatedApplicationId;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private Instant readAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    // Helper methods
    public void markAsRead() {
        this.isRead = true;
        this.readAt = Instant.now();
    }
}