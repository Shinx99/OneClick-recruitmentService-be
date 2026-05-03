package com.onceClick.recruitmentService.features.chatbot.service;

import com.onceClick.recruitmentService.features.chatbot.dto.AiChatResponseDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatWsEventDto;
import com.onceClick.recruitmentService.features.chatbot.dto.TypingDto;
import com.onceClick.recruitmentService.features.chatbot.dto.TypingEventDto;
import com.onceClick.recruitmentService.shared.persistence.entity.AiChatConversation;
import com.onceClick.recruitmentService.shared.persistence.repository.AiChatConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final AiChatConversationRepository conversationRepository;

    public void publishToConversation(String eventType, AiChatResponseDto response) {
        AiChatWsEventDto event = buildEvent(eventType, response);
        messagingTemplate.convertAndSend("/topic/chat/" + response.getConversationId(), event);
        log.debug("Published {} to conversation {}", eventType, response.getConversationId());
    }

    public void publishToUser(UUID conversationId, String eventType, AiChatResponseDto response) {
        AiChatConversation conv = conversationRepository.findById(conversationId)
                .orElseThrow();

        log.info("📤 Publishing to user {}: eventType={}, conversationId={}",
                conv.getUserId(), eventType, conversationId);

        messagingTemplate.convertAndSendToUser(
                conv.getUserId().toString(),
                "/queue/messages",
                buildEvent(eventType, response)
        );
    }

    public void publishToAdmin(UUID conversationId, String eventType, AiChatResponseDto response) {
        AiChatConversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        log.info("📤 Publishing to admin: conversationId={}, eventType={}, assignedAdminId={}, status={}",
                conversationId, eventType, conv.getAssignedAdminId(), conv.getStatus());

        if (conv.getAssignedAdminId() != null) {
            // Nếu đã có admin assigned, gửi trực tiếp đến admin đó
            messagingTemplate.convertAndSendToUser(
                    conv.getAssignedAdminId().toString(),
                    "/queue/admin/messages",
                    buildEvent(eventType, response)
            );
            log.debug("Published {} to assigned admin {}", eventType, conv.getAssignedAdminId());
        } /*else {
            // QUAN TRỌNG: Nếu chưa có admin assigned (status = handoff)
            // Gửi broadcast đến TẤT CẢ admin đang online
            log.info("No admin assigned for conversation {}, broadcasting to all admins", conversationId);

            // Gửi đến waiting list topic để cập nhật danh sách
            messagingTemplate.convertAndSend("/topic/admin/waiting-conversations", conversationId);

            // Gửi broadcast tin nhắn đến tất cả admin
            messagingTemplate.convertAndSend("/topic/admin/broadcast",
                    buildEvent(eventType, response));
        }*/
    }

    // Thêm method mới để gửi broadcast đến tất cả admin
    public void publishToAllAdmins(String eventType, AiChatResponseDto response) {
        log.info("📤 Broadcasting {} to all admins for conversation {}", eventType, response.getConversationId());
        messagingTemplate.convertAndSend("/topic/admin/broadcast",
                buildEvent(eventType, response));
    }

    public void publishAdminWaitingUpdate(UUID conversationId) {
        log.debug("Publishing waiting update for conversation {}", conversationId);
        messagingTemplate.convertAndSend("/topic/admin/waiting-conversations", conversationId);
    }

    public void publishTypingStatus(UUID conversationId, UUID userId, boolean isTyping) {
        // Dùng TypingEventDto cho event gửi đi
        TypingEventDto typingEvent = TypingEventDto.builder()
                .eventType("TYPING")
                .conversationId(conversationId)
                .userId(userId)
                .isTyping(isTyping)
                .timestamp(Instant.now())
                .build();

        messagingTemplate.convertAndSend("/topic/chat/" + conversationId, typingEvent);
        log.debug("Published typing status for conversation {}: isTyping={}", conversationId, isTyping);
    }

    private AiChatWsEventDto buildEvent(String eventType, AiChatResponseDto response) {
        return AiChatWsEventDto.builder()
                .eventType(eventType)
                .conversationId(response.getConversationId())
                .payload(response)
                .timestamp(Instant.now())
                .build();
    }
}