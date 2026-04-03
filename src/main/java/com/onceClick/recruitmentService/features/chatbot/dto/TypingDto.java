package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingDto {
    private UUID conversationId;
    private boolean isTyping;
}