package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// AiChatResponseDto.java
@Data
@Builder
public class AiChatResponseDto {
    private UUID conversationId;
    private List<AiChatMessageDto> messages;
    private String status; // open, handoff, closed
    private UUID assignedAdminId;
}
