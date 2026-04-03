package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AdminChatSendDto {
    private UUID conversationId;
    private String message;
}