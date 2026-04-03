package com.onceClick.recruitmentService.features.chatbot.service;

import com.onceClick.recruitmentService.features.chatbot.dto.*;

import java.util.List;
import java.util.UUID;

// AiChatService.java
public interface AiChatService {
    AiChatResponseDto createOrContinueConversation(AiChatCreateDto dto, String chanel, UUID userId,
                                                   String userType);

    AiChatHistoryDto getMyOpenConversation();

    AiChatHistoryDto getMyConversationHistory(UUID conversationId);

    AiChatResponseDto requestHandoffToAdmin(UUID userId,
                                            String userType);

    List<AiChatConversationSummaryDto> getWaitingConversations();

    AiChatHistoryDto getAdminConversationHistory(UUID conversationId);

    AiChatResponseDto claimConversation(UUID conversationId, UUID adminId, String userType);

    void closeConversationAsAdmin(UUID conversationId);

    AiChatResponseDto sendAdminMessage(UUID conversationId, String message, UUID userId, String userType);

    //suport chat realtime
    void markMessagesAsRead(UUID conversationId, UUID userId);
    List<AiChatMessageDto> getUnreadMessages(UUID conversationId, UUID userId);

}

/**
 AiChatResponseDto createOrContinueConversation(AiChatCreateDto dto);
 AiChatResponseDto requestHandoffToAdmin();
 AiChatResponseDto claimConversation(UUID conversationId);
 AiChatResponseDto sendAdminMessage(UUID conversationId, String message);
 void closeConversationAsAdmin(UUID conversationId);

 AiChatHistoryDto getMyOpenConversation();
 AiChatHistoryDto getMyConversationHistory(UUID conversationId);
 AiChatHistoryDto getAdminConversationHistory(UUID conversationId);
 List<AiChatConversationSummaryDto> getWaitingConversations();

 */