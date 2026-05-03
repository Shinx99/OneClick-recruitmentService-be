package com.onceClick.recruitmentService.features.chatbot.service;

import com.onceClick.recruitmentService.features.chatbot.dto.*;
import com.onceClick.recruitmentService.features.user.service.UserService;
import com.onceClick.recruitmentService.infrastructure.ai.AiService;
import com.onceClick.recruitmentService.infrastructure.ai.prompt.AiPrompts;
import com.onceClick.recruitmentService.shared.persistence.entity.AiChatConversation;
import com.onceClick.recruitmentService.shared.persistence.entity.AiChatMessage;
import com.onceClick.recruitmentService.shared.persistence.repository.AiChatConversationRepository;
import com.onceClick.recruitmentService.shared.persistence.repository.AiChatMessageRepository;
import com.onceClick.recruitmentService.shared.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
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
    private final AiChatWsPublisher wsPublisher;
    private final UserService userService;

    @Override
    public AiChatResponseDto createOrContinueConversation(AiChatCreateDto dto, UUID userId, String userType) {
//        validateUserInput(dto, userId, userType);

        // Bỏ channel parameter, mặc định là "http" hoặc "websocket" tùy context
        String channel = "http"; // Default, controller sẽ override nếu cần
        AiChatConversation conversation = findOrCreateActiveConversation(userId, userType, channel);

        if (STATUS_HANDOFF.equals(conversation.getStatus()) || STATUS_IN_PROGRESS.equals(conversation.getStatus())) {
            throw new IllegalArgumentException("Conversation has been handed off to admin");
        }

        // Nếu message rỗng, chỉ trả về conversation hiện tại (không xử lý gì thêm)
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            log.info("Empty message received, returning existing conversation: {}", conversation.getConversationId());

            List<AiChatMessage> existingMessages = aiChatMessageRepository
                    .findByConversation_ConversationIdOrderByCreatedAtAsc(conversation.getConversationId());

            return AiChatResponseDto.builder()
                    .conversationId(conversation.getConversationId())
                    .messages(existingMessages.stream()
                            .map(msg -> toDto(msg, conversation.getConversationId()))
                            .toList())
                    .status(conversation.getStatus())
                    .assignedAdminId(conversation.getAssignedAdminId())
                    .build();
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



        AiChatResponseDto response = AiChatResponseDto.builder()
                .conversationId(conversation.getConversationId())
                .messages(List.of(
                        toDto(userMessage, conversation.getConversationId()),
                        toDto(aiMessage, conversation.getConversationId())
                ))
                .status(conversation.getStatus())
                .assignedAdminId(conversation.getAssignedAdminId())
                .build();

        // Push WebSocket để frontend nhận tin nhắn AI ngay lập tức
        wsPublisher.publishToConversation("MESSAGE_CREATED", response);
        log.info("📤 Published AI response to conversation: {}", conversation.getConversationId());

        return response;
    }


    @Override
    public AiChatResponseDto sendMessage(UUID conversationId, String content, UUID senderId, String senderType) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content must not be blank");
        }

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));

        // Kiểm tra conversation có thể nhận tin nhắn không
        if (STATUS_CLOSED.equals(conv.getStatus())) {
            throw new IllegalStateException("Conversation is closed");
        }

        // Kiểm tra quyền gửi tin nhắn
        if (TYPE_ADMIN.equals(senderType)) {
            if (conv.getAssignedAdminId() == null) {
                conv.setAssignedAdminId(senderId);
                conv.setStatus(STATUS_IN_PROGRESS);
                aiChatConversationRepository.save(conv);
            } else if (!conv.getAssignedAdminId().equals(senderId)) {
                throw new IllegalArgumentException("Conversation already claimed by another admin");
            }
        } else {
            if (!conv.getUserId().equals(senderId)) {
                throw new IllegalArgumentException("User not authorized to send message to this conversation");
            }
            // Không tự động chuyển sang HANDOFF khi gửi tin nhắn
        }

        // Tạo danh sách messages cho response
        List<AiChatMessageDto> messageDtos = new ArrayList<>();

        // 1. Lưu và thêm user message
        AiChatMessage userMessage = saveMessage(conv, senderId, senderType, MESSAGE_TEXT, content.trim());
        messageDtos.add(toDto(userMessage, conv.getConversationId()));

        // 2. Nếu đang ở chế độ AI (OPEN) và là user, gọi AI để trả lời
        boolean isAIMode = STATUS_OPEN.equals(conv.getStatus()) && !TYPE_ADMIN.equals(senderType);

        if (isAIMode) {
            // Gọi AI để trả lời
            String aiReply = generateAIResponse(conv, content);

            AiChatMessage aiMessage = saveMessage(
                    conv,
                    null,
                    TYPE_AI,
                    MESSAGE_TEXT,
                    aiReply
            );
            messageDtos.add(toDto(aiMessage, conv.getConversationId()));
            log.info("🤖 AI response generated for conversation: {}", conversationId);
        }

        // Cập nhật unread count cho người nhận
        if (TYPE_ADMIN.equals(senderType)) {
            conv.incrementUserUnreadCount();
        } else {
            conv.incrementAdminUnreadCount();
        }

        // 3. Tạo response DUY NHẤT chứa tất cả messages
        AiChatResponseDto response = AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(messageDtos)
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .build();

        // 4. CHỈ PUBLISH 1 LẦN DUY NHẤT
        wsPublisher.publishToConversation("MESSAGE_CREATED", response);
        log.info("📤 Published {} message(s) to conversation: {}", messageDtos.size(), conversationId);

        // 5. Xử lý admin notification nếu cần (chỉ khi conversation ở chế độ handoff/in_progress)
        if (!TYPE_ADMIN.equals(senderType)) {
            if (STATUS_HANDOFF.equals(conv.getStatus()) || STATUS_IN_PROGRESS.equals(conv.getStatus())) {

                /*AiChatResponseDto notificationResponse = AiChatResponseDto.builder()
                        .conversationId(conv.getConversationId())
                        .messages(List.of())  // Không có message content
                        .status(conv.getStatus())
                        .assignedAdminId(conv.getAssignedAdminId())
                        .build();
                */
                // Gửi broadcast đến tất cả admin online
                wsPublisher.publishToAllAdmins("USER_MESSAGE", response);
                log.info("📤 Broadcasted user message to all admins for conversation: {}", conversationId);

                // Nếu đã có admin assigned, gửi riêng
                if (conv.getAssignedAdminId() != null) {
                    wsPublisher.publishToAdmin(conv.getConversationId(), "USER_MESSAGE", response);
                    log.info("📤 Sent user message to assigned admin: {}", conv.getAssignedAdminId());
                }
            }
        }

        // 6. Nếu là admin gửi tin nhắn, gửi riêng đến user
        if (TYPE_ADMIN.equals(senderType)) {
            wsPublisher.publishToUser(conv.getConversationId(), "ADMIN_MESSAGE", response);
            log.info("📤 Sent admin message to user for conversation: {}", conversationId);
        }

        return response;
    }

    @Override
    public AiChatResponseDto requestHandoffToAdmin(UUID userId, String userType) {
        AiChatConversation conv = aiChatConversationRepository
                .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_OPEN)
                .orElseGet(() -> aiChatConversationRepository
                        .findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(userId, userType, STATUS_IN_PROGRESS)
                        .orElseThrow(() -> new IllegalArgumentException("No active conversation found")));

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

        AiChatResponseDto response = AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of(toDto(systemMessage, conv.getConversationId())))
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .build();

        // THÊM: Push WebSocket events
        /*
        wsPublisher.publishToUser(conv.getConversationId(), "HANDOFF_REQUESTED", response);
        wsPublisher.publishToAdmin(conv.getConversationId(), "HANDOFF_REQUESTED", response);
        wsPublisher.publishAdminWaitingUpdate(conv.getConversationId());
        */

        // Chỉ gửi system message đến USER
        wsPublisher.publishToUser(conv.getConversationId(), "SYSTEM_MESSAGE", response);

        // Gửi notification riêng cho admin (không có message content)
        AiChatResponseDto adminNotification = AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of()) // Không có message content
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .build();
        wsPublisher.publishToAdmin(conv.getConversationId(), "HANDOFF_REQUESTED", adminNotification);
        wsPublisher.publishAdminWaitingUpdate(conv.getConversationId());

        return response;
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

        AiChatResponseDto response = AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of(toDto(systemMessage, conv.getConversationId())))
                .status(conv.getStatus())
                .assignedAdminId(adminId)
                .build();

        // THÊM: Push WebSocket events
        /*wsPublisher.publishToUser(conversationId, "ADMIN_CLAIMED", response);
        wsPublisher.publishToAdmin(conversationId, "CLAIMED", response);
        wsPublisher.publishAdminWaitingUpdate(conversationId);*/

        // Chỉ gửi system message đến USER
        wsPublisher.publishToUser(conversationId, "SYSTEM_MESSAGE", response);

        // Gửi notification riêng cho admin
        AiChatResponseDto adminNotification = AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of())
                .status(conv.getStatus())
                .assignedAdminId(adminId)
                .build();

        wsPublisher.publishToUser(conversationId, "ADMIN_CLAIMED", response);
        wsPublisher.publishToAdmin(conversationId, "CLAIMED", adminNotification);
        wsPublisher.publishAdminWaitingUpdate(conversationId);


        return response;
    }

    @Override
    public AiChatResponseDto sendAdminMessage(UUID conversationId, String message, UUID adminId, String userType) {
        // Delegate to sendMessage
        return sendMessage(conversationId, message, adminId, userType);
    }

    // Sửa các method query để dùng CurrentUser bên trong
    @Override
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
                                .orElse(null)));

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
    public List<AiChatConversationSummaryDto> getMyAssignedConversations() {
        UUID adminId = currentUser.getCurrentAccountId();
        return aiChatConversationRepository
                .findByAssignedAdminIdAndStatusIn(adminId, List.of(STATUS_IN_PROGRESS, STATUS_HANDOFF))
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiChatConversationSummaryDto> getAdminConversationsByStatus(String status) {
        requireAdmin();
        UUID adminId = currentUser.getCurrentAccountId();

        if (!List.of(STATUS_HANDOFF, STATUS_IN_PROGRESS, STATUS_CLOSED).contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        if (STATUS_HANDOFF.equals(status)) {
            return aiChatConversationRepository
                    .findByStatusOrderByLastMessageAtDesc(status)
                    .stream()
                    .map(this::toSummaryDto)
                    .toList();
        }

        return aiChatConversationRepository
                .findByAssignedAdminIdAndStatusOrderByLastMessageAtDesc(adminId, status)
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminConversationsGroupDto getAdminConversationsGrouped() {
        requireAdmin();
        UUID adminId = currentUser.getCurrentAccountId();

        return AdminConversationsGroupDto.builder()
                .waiting(aiChatConversationRepository
                        .findByStatusOrderByLastMessageAtDesc(STATUS_HANDOFF)
                        .stream().map(this::toSummaryDto).toList())
                .inProgress(aiChatConversationRepository
                        .findByAssignedAdminIdAndStatusOrderByLastMessageAtDesc(adminId, STATUS_IN_PROGRESS)
                        .stream().map(this::toSummaryDto).toList())
                .closed(aiChatConversationRepository
                        .findByAssignedAdminIdAndStatusOrderByLastMessageAtDesc(adminId, STATUS_CLOSED)
                        .stream().map(this::toSummaryDto).toList())
                .build();
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
    @Transactional(readOnly = true)
    public AiChatHistoryDto getMyConversationHistory(UUID conversationId) {
        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        AiChatConversation conv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conv.getUserId().equals(userId) || !conv.getUserType().equals(userType)) {
            throw new IllegalArgumentException("You do not have permission to view this conversation");
        }

        // ùng Page thay vì List
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").ascending());
        Page<AiChatMessage> messagePage = aiChatMessageRepository
                .findByConversation_ConversationIdOrderByCreatedAtAsc(conversationId, pageable);

        AiChatHistoryDto dto = new AiChatHistoryDto();
        dto.setConversationId(conv.getConversationId());
        dto.setMessages(messagePage.getContent().stream()
                .map(msg -> toDto(msg, conv.getConversationId()))
                .toList());

        // Page có sẵn totalElements và hasNext
        dto.setTotalMessages(messagePage.getTotalElements());
        dto.setHasMore(messagePage.hasNext());

        if (!messagePage.getContent().isEmpty()) {
            AiChatMessage lastMessage = messagePage.getContent().get(messagePage.getContent().size() - 1);
            dto.setLastMessagePreview(lastMessage.getContent());
            dto.setLastMessageAt(lastMessage.getCreatedAt());
        }

        return dto;
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

    @Override
    @Transactional(readOnly = true)
    public MessagePageDto getMessagesWithPagination(UUID conversationId, int page, int size) {
        // Validate page and size
        int validatedPage = Math.max(page, 0);
        int validatedSize = Math.min(size, 100); // Max 100 messages per page

        // Create pageable với sort giảm dần (message mới nhất lên đầu)
        Pageable pageable = PageRequest.of(validatedPage, validatedSize,
                Sort.by("createdAt").descending());

        // Get page from repository
        Page<AiChatMessage> messagePage = aiChatMessageRepository
                .findByConversation_ConversationIdOrderByCreatedAtDesc(conversationId, pageable);

        // Convert to DTOs
        List<AiChatMessageDto> messageDtos = messagePage.getContent()
                .stream()
                .map(msg -> toDto(msg, conversationId))
                .toList();

        // Build response
        return MessagePageDto.builder()
                .messages(messageDtos)
                .currentPage(messagePage.getNumber())
                .totalPages(messagePage.getTotalPages())
                .totalMessages(messagePage.getTotalElements())
                .pageSize(messagePage.getSize())
                .hasNext(messagePage.hasNext())
                .hasPrevious(messagePage.hasPrevious())
                .oldestMessageAt(messagePage.getContent().isEmpty() ? null :
                        messagePage.getContent().get(messagePage.getContent().size() - 1).getCreatedAt())
                .newestMessageAt(messagePage.getContent().isEmpty() ? null :
                        messagePage.getContent().get(0).getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiChatMessageDto> loadMoreMessages(UUID conversationId, Instant beforeDate, int limit) {
        int validatedLimit = Math.min(limit, 50); // Max 50 messages per load more

        Pageable pageable = PageRequest.of(0, validatedLimit,
                Sort.by("createdAt").descending());

        List<AiChatMessage> messages = aiChatMessageRepository
                .findByConversation_ConversationIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                        conversationId, beforeDate, pageable);

        return messages.stream()
                .map(msg -> toDto(msg, conversationId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AiChatMessageDto> getNewMessagesSince(UUID conversationId, Instant sinceDate) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by("createdAt").ascending());

        List<AiChatMessage> messages = aiChatMessageRepository
                .findByConversation_ConversationIdAndCreatedAtAfterOrderByCreatedAtAsc(
                        conversationId, sinceDate, pageable);

        return messages.stream()
                .map(msg -> toDto(msg, conversationId))
                .toList();
    }

    @Override
    @Transactional
    public AiChatResponseDto closeConversationAsUser(UUID conversationId) {
        UUID userId = currentUser.getCurrentAccountId();
        String userType = currentUser.getCurrentUserType();

        log.info("🔵 User {} ({}) closing conversation {}", userId, userType, conversationId);

        // Tìm conversation cần đóng
        AiChatConversation oldConv = aiChatConversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found: " + conversationId));

        // Kiểm tra quyền
        if (!oldConv.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to close this conversation");
        }

        String oldStatus = oldConv.getStatus();
        UUID oldAdminId = oldConv.getAssignedAdminId();

        // 1. Đánh dấu conversation cũ là CLOSED
        oldConv.setStatus(STATUS_CLOSED);
        oldConv.setAssignedAdminId(oldAdminId);
        oldConv.setUpdatedAt(Instant.now());
        aiChatConversationRepository.save(oldConv);

        // Thêm tin nhắn system vào conversation cũ
        String systemMessageContent;
        if (STATUS_HANDOFF.equals(oldStatus)) {
            systemMessageContent = "Bạn đã hủy yêu cầu hỗ trợ. Cuộc trò chuyện đã kết thúc.";
        } else if (STATUS_IN_PROGRESS.equals(oldStatus)) {
            systemMessageContent = "Cuộc trò chuyện đã kết thúc. Cảm ơn bạn đã sử dụng dịch vụ!";
        } else {
            systemMessageContent = "Cuộc trò chuyện đã được đóng.";
        }

        AiChatMessage systemMessage = saveMessage(
                oldConv,
                userId,
                TYPE_SYSTEM,
                MESSAGE_TEXT,
                systemMessageContent
        );

        // Tạo response cho conversation cũ
        AiChatResponseDto oldConvResponse = AiChatResponseDto.builder()
                .conversationId(oldConv.getConversationId())
                .messages(List.of(toDto(systemMessage, oldConv.getConversationId())))
                .status(STATUS_CLOSED)
                .assignedAdminId(null)
                .build();

        // Tạo notification cho admin (không có message content)
        AiChatResponseDto adminNotification = AiChatResponseDto.builder()
                .conversationId(oldConv.getConversationId())
                .messages(List.of()) // Không có message content
                .status(STATUS_CLOSED)
                .assignedAdminId(null)
                .build();

        // 2. Tạo conversation MỚI cho user chat với AI
        AiChatConversation newConv = createNewConversation(userId, userType, "web");

        // Lấy messages của conversation mới (đã có welcome message từ createNewConversation)
        List<AiChatMessage> newMessages = aiChatMessageRepository
                .findByConversation_ConversationIdOrderByCreatedAtAsc(newConv.getConversationId());

        AiChatResponseDto newConvResponse = AiChatResponseDto.builder()
                .conversationId(newConv.getConversationId())
                .messages(newMessages.stream()
                        .map(msg -> toDto(msg, newConv.getConversationId()))
                        .toList())
                .status(STATUS_OPEN)
                .assignedAdminId(null)
                .build();

        // Push WebSocket events cho conversation cũ
//        wsPublisher.publishToUser(oldConv.getConversationId(), "CONVERSATION_CLOSED", oldConvResponse);
        wsPublisher.publishToUser(oldConv.getConversationId(), "SYSTEM_MESSAGE", oldConvResponse);

        wsPublisher.publishToAllAdmins("CONVERSATION_CLOSED", adminNotification);


        if (oldAdminId != null) {
//            wsPublisher.publishToAdmin(oldConv.getConversationId(), "CONVERSATION_CLOSED", oldConvResponse);
            wsPublisher.publishToAdmin(oldConv.getConversationId(), "CONVERSATION_CLOSED", adminNotification);
        } else if (STATUS_HANDOFF.equals(oldStatus)) {
            // Nếu đang ở trạng thái HANDOFF (chưa có admin claim), gửi HANDOFF_REQUESTED để admin refresh waiting list
            wsPublisher.publishToAdmin(oldConv.getConversationId(), "HANDOFF_REQUESTED", adminNotification);
        }

        wsPublisher.publishAdminWaitingUpdate(oldConv.getConversationId());

        log.info("✅ User {} closed conversation {}, created new conversation {}",
                userId, conversationId, newConv.getConversationId());

        // Trả về conversation mới cho frontend
        return newConvResponse;
    }

    // Thêm method helper cho welcome back message
    private String getWelcomeBackMessage() {
        return "Chào mừng bạn quay trở lại! Tôi là trợ lý AI của OnceClick.\n\n" +
                "Tôi vẫn sẵn sàng hỗ trợ bạn. Hãy cho tôi biết bạn cần giúp gì nhé!\n\n" +
                "💡 *Gợi ý:* Nếu cần hỗ trợ từ admin, bạn có thể yêu cầu lại bất cứ lúc nào.";
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

        AiChatMessage systemMessage = saveMessage(
                conv,
                adminId,
                TYPE_SYSTEM,
                MESSAGE_TEXT,
                "Cuộc trò chuyện đã được đóng."
        );

        AiChatResponseDto response = AiChatResponseDto.builder()
                .conversationId(conv.getConversationId())
                .messages(List.of(toDto(systemMessage, conv.getConversationId())))
                .status(conv.getStatus())
                .assignedAdminId(conv.getAssignedAdminId())
                .build();

        // THÊM: Push WebSocket events
        wsPublisher.publishToUser(conversationId, "CONVERSATION_CLOSED", response);
        wsPublisher.publishToAdmin(conversationId, "CONVERSATION_CLOSED", response);
    }


    // ... các method helper giữ nguyên

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
        /*if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("message must not be blank");
        }*/
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



    private AiChatConversation createNewConversation(UUID userId, String userType, String channel) {
        Instant now = Instant.now();

        String validChannel;
        if ("http".equals(channel)) {
            validChannel = "http";
        } else {
            validChannel = "websocket";  // Mặc định
        }

        AiChatConversation conversation = AiChatConversation.builder()
                .userId(userId)
                .userType(userType)
                .status(STATUS_OPEN)
                .channel(validChannel)
                .createdAt(now)
                .updatedAt(now)
                .lastMessageAt(now)  // Set initial value
                .userUnreadCount(0)
                .adminUnreadCount(0)
                .build();

        conversation = aiChatConversationRepository.save(conversation);

        // Tự động gửi tin nhắn chào từ AI
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
        // 1. Lưu message
        AiChatMessage message = aiChatMessageRepository.save(
                AiChatMessage.builder()
                        .conversation(conversation)
                        .senderId(senderId)
                        .senderType(senderType)
                        .messageType(messageType)
                        .content(content)
                        .build()
        );

        // 2. FORCE lấy createdAt - nếu null thì dùng Instant.now()
        Instant messageCreatedAt = message.getCreatedAt();
        if (messageCreatedAt == null) {
            messageCreatedAt = Instant.now();
            log.warn("message.getCreatedAt() is null, using current time");
        }

        // 3. LUÔN set last_message_at cho conversation
        conversation.setLastMessageAt(messageCreatedAt);
        conversation.setUpdatedAt(Instant.now());

        // 4. Lưu conversation
        aiChatConversationRepository.save(conversation);

        log.debug("Saved message {} for conversation {}, last_message_at={}",
                message.getMessageId(), conversation.getConversationId(), messageCreatedAt);

        return message;
    }


    private AiChatHistoryDto buildHistoryDto(AiChatConversation conv) {
        List<AiChatMessage> messages = aiChatMessageRepository
                .findByConversation_ConversationIdOrderByCreatedAtAsc(conv.getConversationId());

        // Lấy thông tin user
        UserService.UserInfo userInfo = userService.getUserInfo(conv.getUserId(), conv.getUserType());

        AiChatHistoryDto dto = new AiChatHistoryDto();
        dto.setConversationId(conv.getConversationId());
        dto.setUserId(conv.getUserId());
        dto.setUserType(conv.getUserType());
        dto.setUserFullName(userInfo != null ? userInfo.fullName() : null);
        dto.setUserEmail(userInfo != null ? userInfo.email() : null);
        dto.setUserAvatar(userInfo != null ? userInfo.avatarUrl() : null);
        dto.setMessages(messages.stream()
                .map(msg -> toDto(msg, conv.getConversationId()))
                .toList());
        dto.setTotalMessages(messages.size());
        dto.setHasMore(false);

        if (!messages.isEmpty()) {
            AiChatMessage lastMessage = messages.get(messages.size() - 1);
            dto.setLastMessagePreview(lastMessage.getContent());
            dto.setLastMessageAt(lastMessage.getCreatedAt());
        }

        return dto;
    }



    private String generateAIResponse(AiChatConversation conv, String userMessage) {
        // Lấy lịch sử chat gần đây
        List<AiChatMessageDto> history = aiChatMessageRepository
                .findTop10ByConversation_ConversationIdOrderByCreatedAtDesc(conv.getConversationId())
                .stream()
                .map(msg -> toDto(msg, conv.getConversationId()))
                .toList();

        String prompt = aiPrompts.buildPromptForChat(
                userMessage,
                history,
                "Hướng dẫn user sử dụng website OnceClick và hỗ trợ chuyển admin khi cần"
        );

        try {
            return deepSeekService.chat("chatbot", prompt);
        } catch (Exception e) {
            log.error("AI chat failed", e);
            return "Xin lỗi, hệ thống AI đang bận. Bạn vui lòng thử lại sau hoặc yêu cầu gặp admin để được hỗ trợ.";
        }
    }

    private AiChatConversationSummaryDto toSummaryDto(AiChatConversation conv) {
        try {
            AiChatMessage lastMessage = aiChatMessageRepository
                    .findFirstByConversation_ConversationIdOrderByCreatedAtDesc(conv.getConversationId())
                    .orElse(null);

            // Lấy thông tin user từ UserService
            UserService.UserInfo userInfo = userService.getUserInfo(conv.getUserId(), conv.getUserType());

            return AiChatConversationSummaryDto.builder()
                    .conversationId(conv.getConversationId())
                    .userId(conv.getUserId())
                    .userType(conv.getUserType())
                    .userFullName(userInfo != null ? userInfo.fullName() : null)
                    .userEmail(userInfo != null ? userInfo.email() : null)
                    .userAvatar(userInfo != null ? userInfo.avatarUrl() : null)
                    .status(conv.getStatus())
                    .assignedAdminId(conv.getAssignedAdminId())
                    .lastMessagePreview(lastMessage != null ? lastMessage.getContent() : null)
                    .lastMessageAt(lastMessage != null ? lastMessage.getCreatedAt() : null)
                    .build();
        } catch (Exception e) {
            log.error("Error converting conversation: {}", conv.getConversationId(), e);
            throw e;
        }
    }

    private AiChatMessageDto toDto(AiChatMessage message, UUID conversationId) {
        return AiChatMessageDto.builder()
                .messageId(message.getMessageId())
                .conversationId(conversationId)
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .metadata(message.getMetadata())
                .createdAt(message.getCreatedAt())
                .isRead(message.isRead())
                .readAt(message.getReadAt())
                .deliveredAt(message.getDeliveredAt())
                .build();
    }
}


