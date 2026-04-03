package com.onceClick.recruitmentService.infrastructure.ai.prompt;

import com.onceClick.recruitmentService.features.chatbot.dto.AiChatMessageDto;
import com.onceClick.recruitmentService.shared.persistence.entity.Job;

import java.util.List;

public interface AiPrompts {
    String getSystemPromptForScanCV(String task);
    String buildJobPrompt(Job job);
    String buildCvJobMatchPrompt(String cvParsedJson, String jobPrompt);

    String getSystemPromptForChat();

    String getOutOfScopePrompt();

    String buildPromptForChat(
            String userMessage,
            List<AiChatMessageDto> history,
            String featureContext
    );

}
