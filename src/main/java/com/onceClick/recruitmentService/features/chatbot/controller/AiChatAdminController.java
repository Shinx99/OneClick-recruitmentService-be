package com.onceClick.recruitmentService.features.chatbot.controller;

import com.onceClick.recruitmentService.features.chatbot.dto.*;
import com.onceClick.recruitmentService.features.chatbot.service.AiChatService;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chatbot/admin")
@RequiredArgsConstructor
public class AiChatAdminController {

    private final AiChatService aiChatService;
    private final CurrentUser currentUser;

    /**
     * Lấy conversations của admin theo status
     * GET /api/chatbot/admin/conversations/status/handoff
     * GET /api/chatbot/admin/conversations/status/in_progress
     * GET /api/chatbot/admin/conversations/status/closed
     */
    @GetMapping("/conversations/status/{status}")
    public ResponseEntity<List<AiChatConversationSummaryDto>> getAdminConversationsByStatus(
            @PathVariable String status) {

        if (!List.of("handoff", "in_progress", "closed").contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return ResponseEntity.ok(aiChatService.getAdminConversationsByStatus(status));
    }

    /**
     * Lấy conversations grouped theo status (waiting, in_progress, closed)
     */
    @GetMapping("/conversations/grouped")
    public ResponseEntity<AdminConversationsGroupDto> getAdminConversationsGrouped() {
        return ResponseEntity.ok(aiChatService.getAdminConversationsGrouped());
    }

    /**
     * Lấy conversations đã được assigned cho admin hiện tại
     */
    @GetMapping("/conversations/my-assigned")
    public ResponseEntity<List<AiChatConversationSummaryDto>> getMyAssignedConversations() {
        return ResponseEntity.ok(aiChatService.getMyAssignedConversations());
    }

    /**
     * Lấy conversations đang chờ admin (handoff)
     */
    @GetMapping("/conversations/waiting")
    public ResponseEntity<List<AiChatConversationSummaryDto>> getWaitingConversations() {
        return ResponseEntity.ok(aiChatService.getWaitingConversations());
    }

    /**
     * Lấy tất cả conversations (cho admin)
     * Có thể filter bằng query param ?status=handoff|in_progress|closed
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<AiChatConversationSummaryDto>> getConversations(
            @RequestParam(required = false) String status) {

        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(aiChatService.getAdminConversationsByStatus(status));
        }
        // Default: lấy waiting conversations
        return ResponseEntity.ok(aiChatService.getWaitingConversations());
    }

    /**
     * Lấy chi tiết conversation (lịch sử tin nhắn)
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<AiChatHistoryDto> getConversationHistory(
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(aiChatService.getAdminConversationHistory(conversationId));
    }

    /**
     * Admin get messages with pagination
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
     * Admin load more messages
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
     * Admin nhận conversation (claim)
     */
    @PostMapping("/conversations/{conversationId}/claim")
    public ResponseEntity<AiChatResponseDto> claimConversation(
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(aiChatService.claimConversation(
                conversationId,
                currentUser.getCurrentAccountId(),
                currentUser.getCurrentUserType()
        ));
    }

    /**
     * Admin đóng conversation
     */
    @PostMapping("/conversations/{conversationId}/close")
    public ResponseEntity<Void> closeConversation(
            @PathVariable UUID conversationId
    ) {
        aiChatService.closeConversationAsAdmin(conversationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Admin gửi tin nhắn (REST version, nếu cần)
     */
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<AiChatResponseDto> sendAdminMessage(
            @PathVariable UUID conversationId,
            @RequestBody SendMessageRequest request
    ) {
        return ResponseEntity.ok(aiChatService.sendAdminMessage(
                conversationId,
                request.message(),
                currentUser.getCurrentAccountId(),
                currentUser.getCurrentUserType()
        ));
    }

    // DTO cho request body
    public record SendMessageRequest(String message) {}
}