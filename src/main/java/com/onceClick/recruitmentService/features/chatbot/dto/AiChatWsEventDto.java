package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AiChatWsEventDto {
    private String eventType;
    private UUID conversationId;
    private AiChatResponseDto payload;
    private Instant timestamp;

}