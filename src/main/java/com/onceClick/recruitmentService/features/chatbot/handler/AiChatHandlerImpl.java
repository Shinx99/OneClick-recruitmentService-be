package com.onceClick.recruitmentService.features.chatbot.handler;

import com.onceClick.recruitmentService.features.chatbot.dto.AiChatConversationSummaryDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatCreateDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatHistoryDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatResponseDto;
import com.onceClick.recruitmentService.features.chatbot.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AiChatHandlerImpl implements AiChatHandler {

    private final AiChatService aiChatService;

    @Override
    public AiChatResponseDto createOrContinueConversation(AiChatCreateDto dto, String chanel,   UUID userId,
                                                          String userType) {
        return aiChatService.createOrContinueConversation(dto, chanel,  userId, userType);
    }

    @Override
    public AiChatHistoryDto getMyOpenConversation() {
        return aiChatService.getMyOpenConversation();
    }

    @Override
    public AiChatHistoryDto getMyConversationHistory(UUID conversationId) {
        return aiChatService.getMyConversationHistory(conversationId);
    }

    @Override
    public AiChatResponseDto requestHandoffToAdmin(UUID userId,
                                                   String userType) {
        return aiChatService.requestHandoffToAdmin(userId, userType);
    }

    @Override
    public List<AiChatConversationSummaryDto> getWaitingConversations() {
        return aiChatService.getWaitingConversations();
    }

    @Override
    public AiChatHistoryDto getAdminConversationHistory(UUID conversationId) {
        return aiChatService.getAdminConversationHistory(conversationId);
    }

    @Override
    public AiChatResponseDto claimConversation(UUID conversationId, UUID adminId, String userType) {
        return aiChatService.claimConversation(conversationId, adminId, userType);
    }

    @Override
    public void closeConversationAsAdmin(UUID conversationId) {
        aiChatService.closeConversationAsAdmin(conversationId);
    }

    @Override
    public AiChatResponseDto sendAdminMessage(UUID conversationId, String message, UUID userId, String userType) {
        return aiChatService.sendAdminMessage(conversationId, message, userId, userType);
    }

    @Override
    public void markMessagesAsRead(UUID conversationId, String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        aiChatService.markMessagesAsRead(conversationId, userId);
    }
}