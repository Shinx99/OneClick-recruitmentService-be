package com.onceClick.recruitmentService.features.chatbot.controller;

import com.onceClick.recruitmentService.features.chatbot.dto.AiChatConversationSummaryDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatHistoryDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatResponseDto;
import com.onceClick.recruitmentService.features.chatbot.handler.AiChatHandler;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot/admin")
@RequiredArgsConstructor
public class AiChatAdminController {

    private final AiChatHandler aiChatHandler;
    private final CurrentUser currentUser;

    // AiChatAdminController.java
    @GetMapping("/conversations")
    public ResponseEntity<List<AiChatConversationSummaryDto>> getWaitingConversations() {
        try {
            List<AiChatConversationSummaryDto> result = aiChatHandler.getWaitingConversations();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();  // In chi tiết lỗi ra console
            throw e;
        }
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<AiChatHistoryDto> getConversationHistory(
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(aiChatHandler.getAdminConversationHistory(conversationId));
    }

    @PostMapping("/conversations/{conversationId}/claim")
    public ResponseEntity<AiChatResponseDto> claimConversation(
            @PathVariable UUID conversationId
    ) {
        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        return ResponseEntity.ok(aiChatHandler.claimConversation(conversationId, userId, userType));
    }

    @PostMapping("/conversations/{conversationId}/close")
    public ResponseEntity<Void> closeConversation(
            @PathVariable UUID conversationId
    ) {
        aiChatHandler.closeConversationAsAdmin(conversationId);
        return ResponseEntity.noContent().build();
    }
}