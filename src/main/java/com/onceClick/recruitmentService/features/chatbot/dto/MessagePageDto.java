package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageDto {
    private List<AiChatMessageDto> messages;
    private int currentPage;
    private int totalPages;
    private long totalMessages;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private Instant oldestMessageAt;
    private Instant newestMessageAt;
}