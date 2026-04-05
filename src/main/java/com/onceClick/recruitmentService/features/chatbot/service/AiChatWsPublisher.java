package com.onceClick.recruitmentService.features.chatbot.service;

import com.onceClick.recruitmentService.features.chatbot.dto.AiChatResponseDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatWsEventDto;
import com.onceClick.recruitmentService.shared.persistence.entity.AiChatConversation;
import com.onceClick.recruitmentService.shared.persistence.repository.AiChatConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

// SAU KHI SỬA - AiChatWsPublisher.java
@Service
@RequiredArgsConstructor
public class AiChatWsPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final AiChatConversationRepository conversationRepository;

    // Gửi đến tất cả subscriber của conversation (public)
    public void publishToConversation(String eventType, AiChatResponseDto response) {
        AiChatWsEventDto event = buildEvent(eventType, response);
        messagingTemplate.convertAndSend("/topic/chat/" + response.getConversationId(), event);
    }

    // THÊM: Gửi riêng đến user
    public void publishToUser(UUID conversationId, String eventType, AiChatResponseDto response) {
        AiChatConversation conv = conversationRepository.findById(conversationId)
                .orElseThrow();

        AiChatWsEventDto event = buildEvent(eventType, response);

        // Gửi đến user queue
        messagingTemplate.convertAndSendToUser(
                conv.getUserId().toString(),
                "/queue/messages",
                event
        );
    }

    // THÊM: Gửi đến admin đang xử lý conversation
    public void publishToAdmin(UUID conversationId, String eventType, AiChatResponseDto response) {
        AiChatConversation conv = conversationRepository.findById(conversationId)
                .orElseThrow();

        if (conv.getAssignedAdminId() != null) {
            messagingTemplate.convertAndSendToUser(
                    conv.getAssignedAdminId().toString(),
                    "/queue/admin/messages",
                    buildEvent(eventType, response)
            );
        }

        // Cập nhật admin waiting list
        messagingTemplate.convertAndSend("/topic/admin/waiting-conversations", conversationId);
    }

    // THÊM: Gửi typing indicator
    public void publishTypingStatus(UUID conversationId, UUID userId, boolean isTyping) {
        Map<String, Object> typingEvent = Map.of(
                "conversationId", conversationId,
                "userId", userId,
                "isTyping", isTyping,
                "timestamp", Instant.now()
        );
        messagingTemplate.convertAndSend("/topic/chat/" + conversationId + "/typing", typingEvent);
    }

    private AiChatWsEventDto buildEvent(String eventType, AiChatResponseDto response) {
        return AiChatWsEventDto.builder()
                .eventType(eventType)
                .conversationId(response.getConversationId())
                .payload(response)
                .timestamp(Instant.now())
                .build();
    }

    public void publishAdminWaitingUpdate(UUID conversationId) {
        // Gửi event cập nhật danh sách chờ cho admin
        messagingTemplate.convertAndSend("/topic/admin/waiting-conversations", conversationId);
    }
}