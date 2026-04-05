package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AiChatConversationSummaryDto {
    private UUID conversationId;
    private UUID userId;
    private String userType;
    private String status;
    private UUID assignedAdminId;
    private String lastMessagePreview;
    private Instant lastMessageAt;
}