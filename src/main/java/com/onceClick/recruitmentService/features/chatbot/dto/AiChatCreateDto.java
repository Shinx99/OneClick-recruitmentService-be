package com.onceClick.recruitmentService.features.chatbot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Chat message request")
@Data
public class AiChatCreateDto {

    @Schema(description = "Message content", example = "Hello")
    private String message;
}