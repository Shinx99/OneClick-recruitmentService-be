/*
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

    List<AiChatConversationSummaryDto> getMyAssignedConversations(UUID adminId);

    // conversations theo status cụ thể
    List<AiChatConversationSummaryDto> getAdminConversationsByStatus(String status);

    // Lấy tất cả conversations của admin (phân theo status)
    AdminConversationsGroupDto getAdminConversationsGrouped();

}

*/

package com.onceClick.recruitmentService.features.chatbot.service;

import com.onceClick.recruitmentService.features.chatbot.dto.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AiChatService {

    // Core methods - không cần channel parameter
    AiChatResponseDto createOrContinueConversation(AiChatCreateDto dto, UUID userId, String userType);

    // Send message (cho cả user và admin)
    AiChatResponseDto sendMessage(UUID conversationId, String content, UUID senderId, String senderType);

    // Handoff và Claim
    AiChatResponseDto requestHandoffToAdmin(UUID userId, String userType);
    AiChatResponseDto claimConversation(UUID conversationId, UUID adminId, String userType);

    // Admin actions
    AiChatResponseDto sendAdminMessage(UUID conversationId, String message, UUID adminId, String userType);
    void closeConversationAsAdmin(UUID conversationId);

    // Query methods - dùng CurrentUser bên trong (cho HTTP)
    AiChatHistoryDto getMyOpenConversation();
    AiChatHistoryDto getMyConversationHistory(UUID conversationId);

    // Admin query methods - dùng CurrentUser
    List<AiChatConversationSummaryDto> getWaitingConversations();
    List<AiChatConversationSummaryDto> getMyAssignedConversations();
    List<AiChatConversationSummaryDto> getAdminConversationsByStatus(String status);
    AdminConversationsGroupDto getAdminConversationsGrouped();
    AiChatHistoryDto getAdminConversationHistory(UUID conversationId);

    // Realtime support (dùng userId truyền vào)
    void markMessagesAsRead(UUID conversationId, UUID userId);
    List<AiChatMessageDto> getUnreadMessages(UUID conversationId, UUID userId);

    // Get messages with pagination
    MessagePageDto getMessagesWithPagination(UUID conversationId, int page, int size);

    // Load more messages (before a specific message)
    List<AiChatMessageDto> loadMoreMessages(UUID conversationId, Instant beforeDate, int limit);

    // Get recent messages after a specific time (for realtime sync)
    List<AiChatMessageDto> getNewMessagesSince(UUID conversationId, Instant sinceDate);

    // Close conversation-user
    AiChatResponseDto closeConversationAsUser(UUID conversationId);

}