package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// AiChatHistoryDto.java
@Data
public class AiChatHistoryDto {
    private UUID conversationId;
    private List<AiChatMessageDto> messages;
    private String lastMessagePreview;
    private Instant lastMessageAt;
}
