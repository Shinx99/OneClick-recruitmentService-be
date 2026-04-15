package com.onceClick.recruitmentService.shared.persistence.repository;

import com.onceClick.recruitmentService.shared.persistence.entity.AiChatConversation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiChatConversationRepository extends JpaRepository<AiChatConversation, UUID> {

    Optional<AiChatConversation> findFirstByUserIdAndUserTypeAndStatusOrderByCreatedAtDesc(
            UUID userId,
            String userType,
            String status
    );

    List<AiChatConversation> findByUserIdAndUserTypeOrderByLastMessageAtDesc(
            UUID userId,
            String userType
    );

    List<AiChatConversation> findByStatusOrderByLastMessageAtDesc(
            String status
    );

    List<AiChatConversation> findByAssignedAdminIdOrderByLastMessageAtDesc(
            UUID assignedAdminId
    );

    List<AiChatConversation> findByStatusAndAssignedAdminIdOrderByLastMessageAtDesc(
            String status,
            UUID assignedAdminId
    );

    List<AiChatConversation> findAllByOrderByLastMessageAtDesc(
            Pageable pageable
    );

    List<AiChatConversation> findByAssignedAdminIdAndStatusIn(UUID adminId, List<String> statuses);

    // Thêm method mới - lấy conversation của admin theo status
    List<AiChatConversation> findByAssignedAdminIdAndStatusOrderByLastMessageAtDesc(UUID adminId, String status);

    // Hoặc lấy nhiều status
    List<AiChatConversation> findByAssignedAdminIdAndStatusInOrderByLastMessageAtDesc(UUID adminId, List<String> statuses);


    @Query("SELECT c FROM AiChatConversation c WHERE c.userId = :userId AND c.userType = :userType AND c.status = 'open' ORDER BY c.createdAt DESC")
    Optional<AiChatConversation> findFirstOpenByUserIdAndUserType(@Param("userId") UUID userId, @Param("userType") String userType);
}