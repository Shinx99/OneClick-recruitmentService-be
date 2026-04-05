package com.onceClick.recruitmentService.features.chatbot.service;

import com.onceClick.recruitmentService.features.chatbot.dto.*;
import com.onceClick.recruitmentService.infrastructure.ai.AiService;
import com.onceClick.recruitmentService.infrastructure.ai.prompt.AiPrompts;
import com.onceClick.recruitmentService.shared.persistence.entity.AiChatConversation;
import com.onceClick.recruitmentService.shared.persistence.entity.AiChatMessage;
import com.onceClick.recruitmentService.shared.persistence.repository.AiChatConversationRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.AiChatMessageRepository;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AiChatServiceImpl implements AiChatService {

    private static final String STATUS_OPEN = "open";
    private static final String STATUS_HANDOFF = "handoff";
    private static final String STATUS_IN_PROGRESS = "in_progress";
    private static final String STATUS_CLOSED = "closed";

    private static final String TYPE_ADMIN = "admin";
    private static final String TYPE_AI = "ai";
    private static final String TYPE_SYSTEM = "system";
    private static final String MESSAGE_TEXT = "text";

    private final AiChatConversationRepository aiChatConversationRepository;
    private final AiChatMessageRepository aiChatMessageRepository;
    private final AiService deepSeekService;
    private final AiPrompts aiPrompts;
    private final CurrentUser currentUser;

    @Override
    public AiChatResponseDto createOrContinueConversation(AiChatCreateDto dto, String chanel,  UUID userId,
                                                          String userType) {
        validateUserInput(dto, userId, userType);

        AiChatConversation conversation = findOrCreateActiveConversation(userId, userType, chanel);

        if (STATUS_HANDOFF.equals(conversation.getStatus()) || STATUS_IN_PROGRESS.equals(conversation.getStatus())) {
            throw new IllegalArgumentException("Conversation has been handed off to admin");
        }

        AiChatMessage userMessage = saveMessage(
                conversation,
                userId,
                userType,
                MESSAGE_TEXT,
                dto.getMessage()
        );

        List<AiChatMessageDto> history = aiChatMessageRepository
                .findTop10ByConversation_ConversationIdOrderByCreatedAtDesc(conversation.getConversationId())
                .stream()
                .map(msg -> toDto(msg, conversation.getConversationId()))
                .toList();

        String prompt = aiPrompts.buildPromptForChat(
                dto.getMessage(),
                history,
                "Hướng dẫn user sử dụng website OnceClick và hỗ trợ chuyển admin khi cần"
        );

        String aiReply;
        try {
            aiReply = deepSeekService.chat("chatbot", prompt);
        } catch (Exception e) {
            log.error("AI chat failed", e);
            aiReply = "Xin lỗi, hệ thống AI đang bận. Bạn vui lòng thử lại sau hoặc yêu cầu gặp admin để được hỗ trợ.";
        }

        AiChatMessage aiMessage = saveMessage(
                conversation,
                null,
                TYPE_AI,
                MESSAGE_TEXT,
                aiReply
        );

        return AiChatResponseDto.builder()
                .conversationId(conversation.getConversationId())
                .messages(List.of(
                        toDto(userMessage, conversation.getConversationId()),
                        toDto(aiMessage, conversation.getConversationId())
                ))
                .status(conversation.getStatus())
                .assignedAdminId(conversation.getAssignedAdminId())
                .build();
    }

    @Override
    public AiChatResponseDto requestHandoffToAdmin(UUID userId,String userType) {


        AiChatConversation conv = aiChatConversationRepository
                .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_OPEN)
                .orElseThrow(() -> new IllegalArgumentException("Open conversation not found"));

        conv.setStatus(STATUS_HANDOFF);
        conv.setAssignedAdminId(null);
        aiChatConversationRepository.save(conv);

        AiChatMessage systemMessage = saveMessage(
                conv,
                null,
                TYPE_SYSTEM,
                MESSAGE_TEXT,
                "Yêu cầu của bạn đã được chuyển sang hàng đợi hỗ trợ admin."
        );

        return AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of(toDto(systemMessage, conv.getConversationId())))
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .build();
    }

    @Override
    public AiChatResponseDto claimConversation(UUID conversationId, UUID adminId, String userType) {

        if (!TYPE_ADMIN.equals(userType)) {
            throw new IllegalArgumentException("Only admin can claim conversation");
        }

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!STATUS_HANDOFF.equals(conv.getStatus())) {
            throw new IllegalArgumentException("Conversation is not waiting for admin");
        }

        if (conv.getAssignedAdminId() != null && !conv.getAssignedAdminId().equals(adminId)) {
            throw new IllegalArgumentException("Conversation already claimed by another admin");
        }

        conv.setAssignedAdminId(adminId);
        conv.setStatus(STATUS_IN_PROGRESS);
        aiChatConversationRepository.save(conv);

        AiChatMessage systemMessage = saveMessage(
                conv,
                adminId,
                TYPE_SYSTEM,
                MESSAGE_TEXT,
                "Admin đã tiếp nhận cuộc trò chuyện."
        );

        return AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of(toDto(systemMessage, conv.getConversationId())))
                .status(conv.getStatus())
                .assignedAdminId(adminId)
                .build();
    }

    @Override
    public AiChatResponseDto sendAdminMessage(UUID conversationId, String message, UUID userId,String userType) {

        if (!TYPE_ADMIN.equals(userType)) {
            throw new IllegalArgumentException("Only admin can send message");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("message must not be blank");
        }

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!(STATUS_HANDOFF.equals(conv.getStatus()) || STATUS_IN_PROGRESS.equals(conv.getStatus()))) {
            throw new IllegalArgumentException("Conversation is not available for admin chat");
        }

        if (conv.getAssignedAdminId() == null) {
            conv.setAssignedAdminId(userId);
            conv.setStatus(STATUS_IN_PROGRESS);
        } else if (!conv.getAssignedAdminId().equals(userId)) {
            throw new IllegalArgumentException("Conversation already claimed by another admin");
        }

        aiChatConversationRepository.save(conv);

        AiChatMessage adminMessage = saveMessage(
                conv,
                userId,
                TYPE_ADMIN,
                MESSAGE_TEXT,
                message.trim()
        );

        return AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of(toDto(adminMessage, conv.getConversationId())))
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public AiChatHistoryDto getMyOpenConversation() {
        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        // Tìm conversation theo thứ tự ưu tiên: open -> handoff -> in_progress
        AiChatConversation conv = aiChatConversationRepository
                .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_OPEN)
                .orElseGet(() -> aiChatConversationRepository
                        .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_HANDOFF)
                        .orElseGet(() -> aiChatConversationRepository
                                .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_IN_PROGRESS)
                                .orElse(null)));  // Trả về null thay vì throw exception

        // Nếu không có conversation, trả về DTO rỗng
        if (conv == null) {
            AiChatHistoryDto emptyDto = new AiChatHistoryDto();
            emptyDto.setConversationId(null);
            emptyDto.setMessages(List.of());
            emptyDto.setLastMessagePreview(null);
            emptyDto.setLastMessageAt(null);
            return emptyDto;
        }

        return buildHistoryDto(conv);
    }

    @Override
    @Transactional(readOnly = true)
    public AiChatHistoryDto getMyConversationHistory(UUID conversationId) {
        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conv.getUserId().equals(userId) || !conv.getUserType().equals(userType)) {
            throw new IllegalArgumentException("You do not have permission to view this conversation");
        }

        return buildHistoryDto(conv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiChatConversationSummaryDto> getWaitingConversations() {
        requireAdmin();

        return aiChatConversationRepository.findByStatusOrderByLastMessageAtDesc(STATUS_HANDOFF)
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AiChatHistoryDto getAdminConversationHistory(UUID conversationId) {
        requireAdmin();

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        return buildHistoryDto(conv);
    }

    @Override
    public void closeConversationAsAdmin(UUID conversationId) {
        UUID adminId = currentUser.getCurrentAccountId();
        requireAdmin();

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (conv.getAssignedAdminId() != null && !conv.getAssignedAdminId().equals(adminId)) {
            throw new IllegalArgumentException("Conversation already claimed by another admin");
        }

        conv.setStatus(STATUS_CLOSED);
        conv.setLastMessageAt(Instant.now());
        aiChatConversationRepository.save(conv);

        saveMessage(conv, adminId, TYPE_SYSTEM, MESSAGE_TEXT, "Cuộc trò chuyện đã được đóng.");
    }

    private void validateUserInput(AiChatCreateDto dto, UUID userId, String userType) {
        if (dto == null) {
            throw new IllegalArgumentException("Request body must not be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (userType == null || (!userType.equals("candidate") && !userType.equals("recruiter"))) {
            throw new IllegalArgumentException("userType must be candidate or recruiter");
        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }

    private void requireAdmin() {
        if (!TYPE_ADMIN.equals(currentUser.getCurrentUserType())) {
            throw new IllegalArgumentException("Only admin can perform this action");
        }
    }

    private AiChatConversation findOrCreateActiveConversation(UUID userId, String userType, String chanel) {
        return aiChatConversationRepository
                .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_OPEN)
                .orElseGet(() -> createNewConversation(userId, userType, chanel));
    }



    // AiChatServiceImpl.java - Sửa createNewConversation
    private AiChatConversation createNewConversation(UUID userId, String userType, String chanel) {
        Instant now = Instant.now();

        AiChatConversation conversation = AiChatConversation.builder()
                .userId(userId)
                .userType(userType)
                .status(STATUS_OPEN)
                .channel(chanel)
                .createdAt(now)
                .updatedAt(now)
                .lastMessageAt(now)
                .build();

        conversation = aiChatConversationRepository.save(conversation);

        // ✅ Tự động gửi tin nhắn chào từ AI
        String welcomeMessage = getWelcomeMessageByUserType(userType);
        saveMessage(conversation, null, TYPE_AI, MESSAGE_TEXT, welcomeMessage);

        return conversation;
    }

    private String getWelcomeMessageByUserType(String userType) {
        if ("recruiter".equals(userType)) {
            return "Xin chào Nhà tuyển dụng! Tôi là trợ lý AI của OnceClick.\n\n" +
                    "Tôi có thể hỗ trợ bạn:\n" +
                    "📢 Đăng tin tuyển dụng\n" +
                    "👥 Tìm kiếm ứng viên phù hợp\n" +
                    "📊 Xem thống kê hiệu quả tuyển dụng\n" +
                    "💬 Quản lý tin nhắn từ ứng viên\n\n" +
                    "Bạn cần tôi giúp gì hôm nay?";
        } else {
            return "Xin chào! Tôi là trợ lý AI của OnceClick.\n\n" +
                    "Tôi có thể hỗ trợ bạn:\n" +
                    "🔍 Tìm kiếm việc làm phù hợp\n" +
                    "📝 Hướng dẫn tạo hồ sơ ứng viên\n" +
                    "💼 Thông tin về nhà tuyển dụng\n" +
                    "📄 Tạo CV chuyên nghiệp\n\n" +
                    "Bạn cần tôi giúp gì hôm nay?";
        }
    }

    private AiChatMessage saveMessage(
            AiChatConversation conversation,
            UUID senderId,
            String senderType,
            String messageType,
            String content
    ) {
        AiChatMessage message = aiChatMessageRepository.save(
                AiChatMessage.builder()
                        .conversation(conversation)
                        .senderId(senderId)
                        .senderType(senderType)
                        .messageType(messageType)
                        .content(content)
                        .build()
        );

        conversation.setLastMessageAt(message.getCreatedAt() != null ? message.getCreatedAt() : Instant.now());
        aiChatConversationRepository.save(conversation);

        return message;
    }

    private AiChatHistoryDto buildHistoryDto(AiChatConversation conv) {
        List<AiChatMessage> messages = aiChatMessageRepository
                .findByConversation_ConversationIdOrderByCreatedAtAsc(conv.getConversationId());

        List<AiChatMessageDto> messageDtos = messages.stream()
                .map(msg -> toDto(msg, conv.getConversationId()))
                .toList();

        AiChatHistoryDto dto = new AiChatHistoryDto();
        dto.setConversationId(conv.getConversationId());
        dto.setMessages(messageDtos);

        if (!messages.isEmpty()) {
            AiChatMessage lastMessage = messages.get(messages.size() - 1);
            dto.setLastMessagePreview(lastMessage.getContent());
            // Sửa: giữ nguyên Instant, không chuyển sang LocalDateTime
            dto.setLastMessageAt(lastMessage.getCreatedAt()); // Instant
        }

        return dto;
    }



    private AiChatConversationSummaryDto toSummaryDto(AiChatConversation conv) {
        try {
            AiChatMessage lastMessage = aiChatMessageRepository
                    .findFirstByConversation_ConversationIdOrderByCreatedAtDesc(conv.getConversationId())
                    .orElse(null);

            // Debug
            System.out.println("=== Processing conversation ===");
            System.out.println("Conversation ID: " + conv.getConversationId());
            System.out.println("Status: " + conv.getStatus());
            System.out.println("Last message: " + (lastMessage != null ? lastMessage.getContent() : "null"));
            System.out.println("Last message createdAt: " + (lastMessage != null ? lastMessage.getCreatedAt() : "null"));

            AiChatConversationSummaryDto dto = AiChatConversationSummaryDto.builder()
                    .conversationId(conv.getConversationId())
                    .userId(conv.getUserId())
                    .userType(conv.getUserType())
                    .status(conv.getStatus())
                    .assignedAdminId(conv.getAssignedAdminId())
                    .lastMessagePreview(lastMessage != null ? lastMessage.getContent() : null)
                    .lastMessageAt(lastMessage != null ? lastMessage.getCreatedAt() : null)
                    .build();

            System.out.println("DTO created: " + dto);
            return dto;
        } catch (Exception e) {
            System.err.println("Error converting conversation: " + conv.getConversationId());
            e.printStackTrace();
            throw e;
        }
    }

    private AiChatMessageDto toDto(AiChatMessage message, UUID conversationId) {
        return AiChatMessageDto.builder()
                .conversationId(conversationId)
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .metadata(null)
                .build();
    }


    @Override
    @Transactional
    public void markMessagesAsRead(UUID conversationId, UUID userId) {
        log.debug("Marking messages as read for conversation: {}, user: {}", conversationId, userId);

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));

        // Kiểm tra quyền
        boolean isParticipant = conv.getUserId().equals(userId);
        boolean isAssignedAdmin = conv.getAssignedAdminId() != null &&
                conv.getAssignedAdminId().equals(userId);

        if (!isParticipant && !isAssignedAdmin) {
            throw new IllegalArgumentException("User " + userId + " is not authorized to read messages in conversation " + conversationId);
        }

        // Cập nhật messages
        int updatedCount = aiChatMessageRepository.updateReadStatus(conversationId, userId);

        // Reset unread count
        if (isParticipant) {
            conv.setUserUnreadCount(0);
            conv.setUserLastSeenAt(Instant.now());
        } else if (isAssignedAdmin) {
            conv.setAdminUnreadCount(0);
            conv.setAdminLastSeenAt(Instant.now());
        }

        aiChatConversationRepository.save(conv);

        log.debug("Marked {} messages as read for conversation {} by user {}",
                updatedCount, conversationId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiChatMessageDto> getUnreadMessages(UUID conversationId, UUID userId) {
        return aiChatMessageRepository.findUnreadByConversationAndUser(conversationId, userId)
                .stream()
                .map(msg -> toDto(msg, conversationId))
                .toList();
    }
}

/* private AiChatConversation createNewConversation(UUID userId, String userType, String chanel) {
        Instant now = Instant.now();

        AiChatConversation conversation = AiChatConversation.builder()
                .userId(userId)
                .userType(userType)
                .status(STATUS_OPEN)
                .channel(chanel)
                .createdAt(now)
                .updatedAt(now)
                .lastMessageAt(now)
                .build();

        return aiChatConversationRepository.save(conversation);
    }*/

// AiChatServiceImpl.java
    /*private AiChatConversation createNewConversation(UUID userId, String userType, String chanel) {
        Instant now = Instant.now();

        AiChatConversation conversation = AiChatConversation.builder()
                .userId(userId)
                .userType(userType)
                .status(STATUS_OPEN)
                .channel(chanel)
                .createdAt(now)
                .updatedAt(now)
                .lastMessageAt(now)
                .build();

        conversation = aiChatConversationRepository.save(conversation);

        // Tự động gửi tin nhắn chào
        String welcomeMessage = "Xin chào! Tôi là trợ lý AI của OneClick. Tôi có thể giúp gì cho bạn hôm nay?\n\n" +
                "Tôi có thể hỗ trợ bạn:\n" +
                "🔍 Tìm kiếm việc làm phù hợp\n" +
                "📝 Hướng dẫn tạo hồ sơ ứng viên\n" +
                "💼 Thông tin về nhà tuyển dụng\n" +
                "🆘 Kết nối với admin nếu cần hỗ trợ thêm";

        saveMessage(conversation, null, TYPE_AI, MESSAGE_TEXT, welcomeMessage);

        return conversation;
    }*/

 /*private AiChatConversationSummaryDto toSummaryDto(AiChatConversation conv) {
        AiChatMessage lastMessage = aiChatMessageRepository
                .findFirstByConversation_ConversationIdOrderByCreatedAtDesc(conv.getConversationId())
                .orElse(null);

        return AiChatConversationSummaryDto.builder()
                .conversationId(conv.getConversationId())
                .userId(conv.getUserId())
                .userType(conv.getUserType())
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .lastMessagePreview(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageAt(lastMessage.map(AiChatMessage::getCreatedAt).orElse(null))
                .build();
    }
*/
    /*private AiChatConversationSummaryDto toSummaryDto(AiChatConversation conv) {
        AiChatMessage lastMessage = aiChatMessageRepository
                .findFirstByConversation_ConversationIdOrderByCreatedAtDesc(conv.getConversationId())
                .orElse(null);

        System.out.println("=== Processing conversation ===");
        System.out.println("Conversation ID: " + conv.getConversationId());
        System.out.println("Status: " + conv.getStatus());
        System.out.println("Last message: " + (lastMessage != null ? lastMessage.getContent() : "null"));
        System.out.println("Last message createdAt: " + (lastMessage != null ? lastMessage.getCreatedAt() : "null"));

        return AiChatConversationSummaryDto.builder()
                .conversationId(conv.getConversationId())
                .userId(conv.getUserId())
                .userType(conv.getUserType())
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .lastMessagePreview(lastMessage != null ? lastMessage.getContent() : null)
                // Sửa: giữ nguyên Instant
                .lastMessageAt(lastMessage != null ? lastMessage.getCreatedAt() : null)
                .build();
    }*/

 /*@Override
    @Transactional(readOnly = true)
    public AiChatHistoryDto getMyOpenConversation() {
        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        AiChatConversation conv = aiChatConversationRepository
                .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_OPEN)
                .orElseGet(() -> aiChatConversationRepository
                        .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_HANDOFF)
                        .orElseGet(() -> aiChatConversationRepository
                                .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_IN_PROGRESS)
                                .orElseThrow(() -> new IllegalArgumentException("Active conversation not found"))));

        return buildHistoryDto(conv);
    }*/


    /*private AiChatHistoryDto buildHistoryDto(AiChatConversation conv) {
        List<AiChatMessage> messages = aiChatMessageRepository
                .findByConversation_ConversationIdOrderByCreatedAtAsc(conv.getConversationId());

        List<AiChatMessageDto> messageDtos = messages.stream()
                .map(msg -> toDto(msg, conv.getConversationId()))
                .toList();

        AiChatHistoryDto dto = new AiChatHistoryDto();
        dto.setConversationId(conv.getConversationId());
        dto.setMessages(messageDtos);

        if (!messages.isEmpty()) {
            AiChatMessage lastMessage = messages.get(messages.size() - 1);
            dto.setLastMessagePreview(lastMessage.getContent());
            dto.setLastMessageAt(
                    lastMessage.getCreatedAt() != null
                            ? lastMessage.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                            : null
            );
        }

        return dto;
    }*/
/*@Override
    @Transactional
    public void markMessagesAsRead(UUID conversationId, UUID userId) {
        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow();

        // Chỉ user hoặc admin được đánh dấu đã đọc
        if (!conv.getUserId().equals(userId) && !conv.getAssignedAdminId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized");
        }

        // Cập nhật messages chưa đọc
        aiChatMessageRepository.updateReadStatus(conversationId, userId);
    }*/