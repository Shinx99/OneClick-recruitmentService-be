package com.onceClick.recruitmentService.features.chatbot.controller;

import com.onceClick.recruitmentService.features.chatbot.dto.*;
import com.onceClick.recruitmentService.features.chatbot.handler.AiChatHandler;
import com.onceClick.recruitmentService.features.chatbot.service.AiChatWsPublisher;
import com.onceClick.recruitmentService.shared.security.CustomUserPrincipal;
import com.onceClick.recruitmentService.shared.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AiChatStompController {

    private final AiChatHandler aiChatHandler;
    private final AiChatWsPublisher wsPublisher;

    @MessageMapping("/chat.send")
    public void sendMessage(AiChatCreateDto dto, Principal principal) {
        // Lấy userId từ principal
        UUID userId = getUserIdFromPrincipal(principal);
        String userType = getUserTypeFromPrincipal(principal);
        log.debug("User {} ({}) sending message", userId, userType);

        AiChatResponseDto response = aiChatHandler.createOrContinueConversation(
                dto, "websocket", userId, userType
        );

        wsPublisher.publishToConversation("MESSAGE_CREATED", response);

        if (response.getAssignedAdminId() != null) {
            wsPublisher.publishToAdmin(response.getConversationId(), "USER_MESSAGE", response);
        }
    }


    @MessageMapping("/chat.handoff")
    public void requestHandoff(Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        String userType = getUserTypeFromPrincipal(principal);
        log.debug("User {} ({}) requesting handoff", userId, userType);

        // Truyền userId và userType vào service
        AiChatResponseDto response = aiChatHandler.requestHandoffToAdmin(userId, userType);

        wsPublisher.publishToUser(response.getConversationId(), "HANDOFF_REQUESTED", response);
        wsPublisher.publishToAdmin(response.getConversationId(), "HANDOFF_REQUESTED", response);
    }


    @MessageMapping("/admin.chat.claim")
    public void claimConversation(ConversationActionDto dto, Principal principal) {
        UUID adminId = getUserIdFromPrincipal(principal);
        String userType = getUserTypeFromPrincipal(principal);

        log.debug("Admin {} claiming conversation {}", adminId, dto.getConversationId());

        AiChatResponseDto response = aiChatHandler.claimConversation(dto.getConversationId(), adminId, userType);

        wsPublisher.publishToUser(response.getConversationId(), "ADMIN_CLAIMED", response);
        wsPublisher.publishToAdmin(response.getConversationId(), "CLAIMED", response);
        wsPublisher.publishAdminWaitingUpdate(response.getConversationId());
    }

    @MessageMapping("/admin.chat.send")
    public void sendAdminMessage(AdminChatSendDto dto, Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);
        String userType = getUserTypeFromPrincipal(principal);
        log.info("🔵 Admin type from principal: {}", userType);
        log.debug("Admin {} sending message to conversation {}", userId, userType, dto.getConversationId());

        AiChatResponseDto response = aiChatHandler.sendAdminMessage(
                dto.getConversationId(),
                dto.getMessage(),
                userId,
                userType
        );

        wsPublisher.publishToUser(response.getConversationId(), "ADMIN_MESSAGE", response);
        wsPublisher.publishToAdmin(response.getConversationId(), "MESSAGE_SENT", response);
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(TypingDto dto, Principal principal) {
        UUID userId = getUserIdFromPrincipal(principal);

        wsPublisher.publishTypingStatus(
                dto.getConversationId(),
                userId,
                dto.isTyping()
        );
    }

    @MessageMapping("/chat.read")
    public void markAsRead(ConversationActionDto dto, Principal principal) {
        String userIdStr = principal.getName(); // getName() trả về userId string
        aiChatHandler.markMessagesAsRead(dto.getConversationId(), userIdStr);
    }

    // Helper method để lấy userId từ Principal
    private UUID getUserIdFromPrincipal(Principal principal) {
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUserId();
        }
        // Fallback: lấy từ getName()
        return UUID.fromString(principal.getName());
    }

    // Helper method để lấy userType từ Principal (nếu cần)
    private String getUserTypeFromPrincipal(Principal principal) {
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUserType();
        }
        return "unknown";
    }
}