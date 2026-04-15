package com.onceClick.recruitmentService.features.chatbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Schema(description = "Chat message request")
@Data
@Builder
public class AiChatCreateDto {

    private UUID conversationId;
    @Schema(description = "Message content", example = "Hello")
    private String message;
}