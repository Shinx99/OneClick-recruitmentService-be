package com.onceClick.recruitmentService.features.chatbot.handler;

import com.onceClick.recruitmentService.features.chatbot.dto.AiChatConversationSummaryDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatCreateDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatHistoryDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatResponseDto;

import java.util.List;
import java.util.UUID;

public interface AiChatHandler {

    AiChatResponseDto createOrContinueConversation(AiChatCreateDto dto,
                                                   String chanel,
                                                   UUID userId,
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

    void markMessagesAsRead(UUID conversationId, String userIdStr);
}