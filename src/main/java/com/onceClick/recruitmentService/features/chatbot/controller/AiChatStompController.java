package com.onceClick.recruitmentService.features.chatbot.controller;

import com.onceClick.recruitmentService.features.chatbot.dto.*;
import com.onceClick.recruitmentService.features.chatbot.service.AiChatService;
import com.onceClick.recruitmentService.features.chatbot.service.AiChatWsPublisher;
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

    private final AiChatService aiChatService;  // Đổi từ AiChatHandler sang AiChatService
    private final AiChatWsPublisher wsPublisher;

    @MessageMapping("/chat.send")
    public void sendMessage(AiChatCreateDto dto, Principal principal) {
        UserContext ctx = extractUserContext(principal);
        log.debug("User {} ({}) sending message", ctx.userId, ctx.userType);

        AiChatResponseDto response;

        if (dto.getConversationId() != null) {
            try {
                response = aiChatService.sendMessage(
                        dto.getConversationId(),
                        dto.getMessage(),
                        ctx.userId,
                        ctx.userType
                );
            } catch (IllegalArgumentException e) {
                log.warn("Conversation {} not found or closed, creating new: {}", dto.getConversationId(), e.getMessage());
                response = aiChatService.createOrContinueConversation(dto, ctx.userId, ctx.userType);
            }
        } else {
            response = aiChatService.createOrContinueConversation(dto, ctx.userId, ctx.userType);
        }

        /*// Gửi đến conversation topic
        wsPublisher.publishToConversation("MESSAGE_CREATED", response);

        // Gửi broadcast đến tất cả admin
        wsPublisher.publishToAllAdmins("USER_MESSAGE", response);

        // Nếu có assigned admin, gửi riêng
        if (response.getAssignedAdminId() != null) {
            wsPublisher.publishToAdmin(response.getConversationId(), "USER_MESSAGE", response);
        }*/

        log.info("✅ Message processed, response contains {} messages", response.getMessages().size());
    }


    @MessageMapping("/chat.handoff")
    public void requestHandoff(Principal principal) {
        UserContext ctx = extractUserContext(principal);
        log.debug("User {} ({}) requesting handoff", ctx.userId, ctx.userType);

        AiChatResponseDto response = aiChatService.requestHandoffToAdmin(ctx.userId, ctx.userType);

        wsPublisher.publishToUser(response.getConversationId(), "HANDOFF_REQUESTED", response);
        wsPublisher.publishToAdmin(response.getConversationId(), "HANDOFF_REQUESTED", response);
    }

    @MessageMapping("/admin.chat.claim")
    public void claimConversation(ConversationActionDto dto, Principal principal) {
        UserContext ctx = extractUserContext(principal);
        log.debug("Admin {} claiming conversation {}", ctx.userId, dto.getConversationId());

        AiChatResponseDto response = aiChatService.claimConversation(
                dto.getConversationId(),
                ctx.userId,
                ctx.userType
        );

        wsPublisher.publishToUser(response.getConversationId(), "ADMIN_CLAIMED", response);
        wsPublisher.publishToAdmin(response.getConversationId(), "CLAIMED", response);
        wsPublisher.publishAdminWaitingUpdate(response.getConversationId());
    }

    @MessageMapping("/admin.chat.send")
    public void sendAdminMessage(AdminChatSendDto dto, Principal principal) {
        UserContext ctx = extractUserContext(principal);
        log.debug("Admin {} sending message to conversation {}", ctx.userId, dto.getConversationId());

        AiChatResponseDto response = aiChatService.sendAdminMessage(
                dto.getConversationId(),
                dto.getMessage(),
                ctx.userId,
                ctx.userType
        );

        wsPublisher.publishToUser(response.getConversationId(), "ADMIN_MESSAGE", response);
        wsPublisher.publishToAdmin(response.getConversationId(), "MESSAGE_SENT", response);
    }

    /*@MessageMapping("/chat.typing")
    public void handleTyping(TypingDto dto, Principal principal) {
        UserContext ctx = extractUserContext(principal);
        wsPublisher.publishTypingStatus(dto.getConversationId(), ctx.userId, dto.isTyping());
    }*/
    @MessageMapping("/chat.typing")
    public void handleTyping(TypingDto dto, Principal principal) {
        log.info("📝 Typing event: conversationId={}, isTyping={} (ignored)",
                dto.getConversationId(), dto.isTyping());
        // Không gọi wsPublisher.publishTypingStatus
    }


    @MessageMapping("/chat.read")
    public void markAsRead(ConversationActionDto dto, Principal principal) {
        UserContext ctx = extractUserContext(principal);
        aiChatService.markMessagesAsRead(dto.getConversationId(), ctx.userId);
    }

    // Helper method lấy user context từ Principal
    private UserContext extractUserContext(Principal principal) {
        if (principal instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            return new UserContext(userPrincipal.getUserId(), userPrincipal.getUserType());
        }
        // Fallback: lấy từ getName()
        UUID userId = UUID.fromString(principal.getName());
        return new UserContext(userId, "unknown");
    }

    private record UserContext(UUID userId, String userType) {}
}