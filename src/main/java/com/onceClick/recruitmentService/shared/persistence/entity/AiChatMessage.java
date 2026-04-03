package com.onceClick.recruitmentService.shared.persistence.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
    name = "ai_chat_message",
    indexes = {
            @Index(name = "idx_ai_chat_message_conversation_created",
                    columnList = "conversation_id,created_at"),
            @Index(name = "idx_ai_chat_message_sender_type",
                    columnList = "sender_type"),
            @Index(name = "idx_ai_chat_message_unread",
                    columnList = "conversation_id,is_read,sender_type"),
            @Index(name = "idx_ai_chat_message_realtime_delivery",
                    columnList = "conversation_id,created_at,delivered_at,is_read")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessage {

    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private UUID messageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private AiChatConversation conversation;

    @Column(name = "sender_id")
    private UUID senderId;

    @Column(name = "sender_type", length = 20, nullable = false)
    private String senderType; // candidate | employer | ai | admin | system

    @Column(name = "message_type", length = 20, nullable = false)
    private String messageType = "text"; // text | image | file

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "JSONB")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false,updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private Instant createdAt;

    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean read = false;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    // Helper methods
    public void markAsDelivered() {
        this.deliveredAt = Instant.now();
    }

    public void markAsRead() {
        this.read = true;
        this.readAt = Instant.now();
    }

    public boolean isDelivered() {
        return deliveredAt != null;
    }

}