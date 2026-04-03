package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.AiChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, UUID> {

    List<AiChatMessage> findByConversation_ConversationIdOrderByCreatedAtAsc(
            UUID conversationId
    );

    List<AiChatMessage> findByConversation_ConversationIdOrderByCreatedAtDesc(
            UUID conversationId,
            Pageable pageable
    );

    Optional<AiChatMessage> findFirstByConversation_ConversationIdOrderByCreatedAtDesc(
            UUID conversationId
    );

    List<AiChatMessage> findByConversation_ConversationIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            UUID conversationId,
            Instant createdAt,
            Pageable pageable
    );

    long countByConversation_ConversationId(
            UUID conversationId
    );

    List<AiChatMessage> findTop10ByConversation_ConversationIdOrderByCreatedAtDesc(UUID conversationId);

    // Lấy tin nhắn chưa đọc
    @Query("SELECT m FROM AiChatMessage m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.senderId != :userId AND m.read = false")
    List<AiChatMessage> findUnreadByConversationAndUser(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId
    );

    // Cập nhật trạng thái đã đọc
    @Modifying
    @Transactional
    @Query("UPDATE AiChatMessage m SET m.read = true, m.readAt = CURRENT_TIMESTAMP " +
            "WHERE m.conversation.conversationId = :conversationId " +
            "AND m.senderId != :userId " +
            "AND m.read = false")
    int updateReadStatus(@Param("conversationId") UUID conversationId,
                         @Param("userId") UUID userId);

    // Thêm method count unread messages
    @Query("SELECT COUNT(m) FROM AiChatMessage m " +
            "WHERE m.conversation.conversationId = :conversationId " +
            "AND m.senderId != :userId " +
            "AND m.read = false")
    int countUnreadByConversationAndUser(@Param("conversationId") UUID conversationId,
                                         @Param("userId") UUID userId);
}