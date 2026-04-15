package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypingEventDto {
    private String eventType;
    private UUID conversationId;
    private UUID userId;
    private boolean isTyping;
    private Instant timestamp;
}