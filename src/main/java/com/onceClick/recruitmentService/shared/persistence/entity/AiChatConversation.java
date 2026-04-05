package com.onceClick.recruitmentService.shared.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "ai_chat_conversation",
        indexes = {
                @Index(name = "idx_ai_chat_conversation_user", columnList = "user_id,user_type"),
                @Index(name = "idx_ai_chat_conversation_status", columnList = "status"),
                @Index(name = "idx_ai_chat_conversation_last_message_at", columnList = "last_message_at"),
                @Index(name = "idx_ai_chat_conversation_admin_assigned",
                        columnList = "assigned_admin_id,status,last_message_at"),
                @Index(name = "idx_ai_chat_conversation_user_realtime",
                        columnList = "user_id,status,user_unread_count")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatConversation {

    @Id
    @GeneratedValue
    @Column(name = "conversation_id")
    private UUID conversationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_type", length = 20, nullable = false)
    private String userType; // candidate | employer

    @Column(name = "status", length = 20, nullable = false)
    private String status = "open"; // open | handoff | closed

    @Column(name = "assigned_admin_id")
    private UUID assignedAdminId;

    @Column(name = "channel", length = 20, nullable = false)
    private String channel = "websocket"; // websocket | http

    @Column(name = "last_message_at", nullable = false)
    private Instant lastMessageAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AiChatMessage> messages = new ArrayList<>();

    @Column(name = "user_last_seen_at")
    private Instant userLastSeenAt;

    @Column(name = "admin_last_seen_at")
    private Instant adminLastSeenAt;

    @Column(name = "user_unread_count", columnDefinition = "INTEGER DEFAULT 0")
    private int userUnreadCount = 0;

    @Column(name = "admin_unread_count", columnDefinition = "INTEGER DEFAULT 0")
    private int adminUnreadCount = 0;

    // Helper method để increment unread count
    public void incrementUserUnreadCount() {
        this.userUnreadCount++;
    }

    public void incrementAdminUnreadCount() {
        this.adminUnreadCount++;
    }

    public void resetUserUnreadCount() {
        this.userUnreadCount = 0;
        this.userLastSeenAt = Instant.now();
    }

    public void resetAdminUnreadCount() {
        this.adminUnreadCount = 0;
        this.adminLastSeenAt = Instant.now();
    }

}