// shared/security/UserContextHelper.java
package com.onceClick.recruitmentService.shared.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextHelper {
    
    private final CurrentUser currentUser;
    
    /**
     * Dùng cho HTTP requests - delegate thẳng cho CurrentUser
     */
    public UserContext fromHttp() {
        return new UserContext(
            currentUser.getCurrentAccountId(),
            currentUser.getCurrentUserType()
        );
    }
    
    /**
     * Dùng cho WebSocket messages - lấy từ Principal
     */
    public UserContext fromWebSocket(Principal principal) {
        if (principal instanceof CustomUserPrincipal) {
            CustomUserPrincipal custom = (CustomUserPrincipal) principal;
            return new UserContext(custom.getUserId(), custom.getUserType());
        }
        
        // Fallback an toàn
        log.warn("Principal is not CustomUserPrincipal, falling back to HTTP context");
        return fromHttp();
    }
    
    public record UserContext(UUID userId, String userType) {}
}