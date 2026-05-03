package com.onceClick.recruitmentService.features.chatbot.controller;

import com.onceClick.recruitmentService.features.chatbot.dto.*;
import com.onceClick.recruitmentService.features.chatbot.service.AiChatService;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chatbot/me")
@RequiredArgsConstructor
public class AiChatUserController {

    private final AiChatService aiChatService;  // Đổi từ AiChatHandler sang AiChatService
    private final CurrentUser currentUser;

    /**
     * Lấy messages với phân trang
     * GET /api/chatbot/me/conversations/{conversationId}/messages?page=0&size=20
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessagePageDto> getMessagesWithPagination(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(aiChatService.getMessagesWithPagination(conversationId, page, size));
    }

    /**
     * Load more messages (infinite scroll)
     * GET /api/chatbot/me/conversations/{conversationId}/messages/load-more?beforeDate=2024-01-01T00:00:00Z&limit=20
     */
    @GetMapping("/conversations/{conversationId}/messages/load-more")
    public ResponseEntity<List<AiChatMessageDto>> loadMoreMessages(
            @PathVariable UUID conversationId,
            @RequestParam Instant beforeDate,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(aiChatService.loadMoreMessages(conversationId, beforeDate, limit));
    }

    /**
     * Get new messages since last sync (cho realtime)
     * GET /api/chatbot/me/conversations/{conversationId}/messages/new?sinceDate=2024-01-01T00:00:00Z
     */
    @GetMapping("/conversations/{conversationId}/messages/new")
    public ResponseEntity<List<AiChatMessageDto>> getNewMessagesSince(
            @PathVariable UUID conversationId,
            @RequestParam Instant sinceDate
    ) {
        return ResponseEntity.ok(aiChatService.getNewMessagesSince(conversationId, sinceDate));
    }

    /**
     * Mark messages as read
     * POST /api/chatbot/me/conversations/{conversationId}/read
     */
    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable UUID conversationId) {
        aiChatService.markMessagesAsRead(
                conversationId,
                currentUser.getCurrentAccountId()
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Get unread messages
     * GET /api/chatbot/me/conversations/{conversationId}/unread
     */
    @GetMapping("/conversations/{conversationId}/unread")
    public ResponseEntity<List<AiChatMessageDto>> getUnreadMessages(
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(aiChatService.getUnreadMessages(
                conversationId,
                currentUser.getCurrentAccountId()
        ));
    }



    /**
     * Lấy conversation đang mở của user hiện tại
     */
    @GetMapping("/conversation")
    public ResponseEntity<AiChatHistoryDto> getMyOpenConversation() {
        AiChatHistoryDto history = aiChatService.getMyOpenConversation();

        if (history.getConversationId() == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(history);
    }

    /**
     * Lấy lịch sử conversation theo ID
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<AiChatHistoryDto> getMyConversationHistory(
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(aiChatService.getMyConversationHistory(conversationId));
    }

    /**
     * User tự đóng conversation và quay lại chat với AI
     * POST /api/chatbot/me/conversations/{conversationId}/close
     */
    @PostMapping("/conversations/{conversationId}/close")
    public ResponseEntity<AiChatResponseDto> closeConversation(
            @PathVariable UUID conversationId) {

        log.info("🔵 User requesting to close conversation: {}", conversationId);

        AiChatResponseDto response = aiChatService.closeConversationAsUser(conversationId);

        return ResponseEntity.ok(response);
    }



    /**
     * Yêu cầu chuyển sang admin hỗ trợ
     */
    @PostMapping("/handoff")
    public ResponseEntity<AiChatResponseDto> requestHandoffToAdmin() {
        return ResponseEntity.ok(aiChatService.requestHandoffToAdmin(
                currentUser.getCurrentAccountId(),
                currentUser.getCurrentUserType()
        ));
    }

    /**
     * Tạo conversation mới hoặc tiếp tục conversation cũ
     */
    @PostMapping("/conversation")
    public ResponseEntity<AiChatResponseDto> createConversation() {
        // Tạo conversation mới với tin nhắn rỗng (sẽ có welcome message)
        AiChatCreateDto dto = AiChatCreateDto.builder()
                .message("")  // Tin nhắn rỗng để trigger welcome
                .build();

        return ResponseEntity.ok(aiChatService.createOrContinueConversation(
                dto,
                currentUser.getCurrentAccountId(),
                currentUser.getCurrentUserType()
        ));
    }

    /**
     * Gửi tin nhắn vào conversation
     */
    @PostMapping("/messages")
    public ResponseEntity<AiChatResponseDto> sendMessage(@Valid @RequestBody AiChatCreateDto dto) {
        if (dto.getConversationId() == null) {
            // Nếu chưa có conversationId, tạo mới
            return ResponseEntity.ok(aiChatService.createOrContinueConversation(
                    dto,
                    currentUser.getCurrentAccountId(),
                    currentUser.getCurrentUserType()
            ));
        }

        // Gửi tin nhắn vào conversation existing
        return ResponseEntity.ok(aiChatService.sendMessage(
                dto.getConversationId(),
                dto.getMessage(),
                currentUser.getCurrentAccountId(),
                currentUser.getCurrentUserType()
        ));
    }


}