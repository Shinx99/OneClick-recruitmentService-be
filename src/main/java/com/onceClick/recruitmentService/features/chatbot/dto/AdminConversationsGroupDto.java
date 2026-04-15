// AdminConversationsGroupDto.java
package com.onceClick.recruitmentService.features.chatbot.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AdminConversationsGroupDto {
    private List<AiChatConversationSummaryDto> waiting;    // handoff
    private List<AiChatConversationSummaryDto> inProgress; // in_progress
    private List<AiChatConversationSummaryDto> closed;     // closed
}