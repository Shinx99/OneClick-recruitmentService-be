package com.onceClick.recruitmentService.features.chatbot.controller;

import com.onceClick.recruitmentService.features.chatbot.dto.AiChatCreateDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatHistoryDto;
import com.onceClick.recruitmentService.features.chatbot.dto.AiChatResponseDto;
import com.onceClick.recruitmentService.features.chatbot.handler.AiChatHandler;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chatbot/me")
@RequiredArgsConstructor
public class AiChatUserController {

    private final AiChatHandler aiChatHandler;
    private final CurrentUser currentUser;

    @PostMapping(value = "/messages", consumes = "application/json")
    public ResponseEntity<AiChatResponseDto> createOrContinueConversation(
            @Valid @RequestBody AiChatCreateDto dto
    ) {
        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        return ResponseEntity.ok(
                aiChatHandler.createOrContinueConversation(dto, "http", userId, userType)
        );
    }

    @GetMapping("/conversation")
    public ResponseEntity<AiChatHistoryDto> getMyOpenConversation() {

        AiChatHistoryDto history = aiChatHandler.getMyOpenConversation();

        if (history.getConversationId() == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(aiChatHandler.getMyOpenConversation());
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<AiChatHistoryDto> getMyConversationHistory(
            @PathVariable UUID conversationId
    ) {
        return ResponseEntity.ok(aiChatHandler.getMyConversationHistory(conversationId));
    }

    @PostMapping("/handoff")
    public ResponseEntity<AiChatResponseDto> requestHandoffToAdmin() {

        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        return ResponseEntity.ok(aiChatHandler.requestHandoffToAdmin(userId, userType));
    }
}