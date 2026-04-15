package com.onceClick.recruitmentService.features.chatbot.dto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

// AiChatMessageDto.java
@Data
@Builder
public class AiChatMessageDto {

    private UUID messageId;

    private UUID conversationId;
    private UUID senderId;
    private String senderType; // candidate, employer, ai, admin, system
    private String messageType; // text, image, file
    private String content;
    private Map<String, Object> metadata;

    private Instant createdAt;
    private boolean isRead;
    private Instant readAt;
    private Instant deliveredAt;
}

