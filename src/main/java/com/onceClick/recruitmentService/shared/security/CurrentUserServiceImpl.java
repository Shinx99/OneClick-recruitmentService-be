/*package com.onceClick.recruitmentService.shared.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Service
public class CurrentUserServiceImpl implements CurrentUser {

    @Override
    public UUID getCurrentAccountId() {
        // 1. Thử từ SecurityContext (HTTP requests)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                return UUID.fromString(auth.getName());
            } catch (Exception e) {
                log.debug("Failed to get user from SecurityContext: {}", e.getMessage());
            }
        }

        // 2. Thử từ WebSocket context - LƯU Ý: Chỉ hoạt động trong WebSocket message handling
        // Cách này chỉ dùng được trong các method được gọi từ WebSocket message
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(SimpMessageHeaderAccessor.getCurrentMessage());
        if (accessor != null) {
            Principal principal = accessor.getUser();
            if (principal != null && principal.getName() != null) {
                try {
                    log.debug("Getting user from WebSocket principal: {}", principal.getName());
                    return UUID.fromString(principal.getName());
                } catch (Exception e) {
                    log.error("Invalid user ID format from WebSocket: {}", principal.getName());
                }
            }
        }

        throw new IllegalStateException("Cannot determine current user - no authentication found");
    }

    @Override
    public String getCurrentUserType() {
        // 1. Thử từ SecurityContext (HTTP requests)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userType = extractUserTypeFromAuthorities(authentication);
            if (userType != null) return userType;
        }

        // 2. Thử từ WebSocket context
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(SimpMessageHeaderAccessor.getCurrentMessage());
        if (accessor != null) {
            Principal principal = accessor.getUser();
            if (principal instanceof CustomUserPrincipal) {
                return ((CustomUserPrincipal) principal).getUserType();
            }
            // Thử lấy từ session attributes
            if (accessor.getSessionAttributes() != null) {
                String userType = (String) accessor.getSessionAttributes().get("userType");
                if (userType != null) return userType;
            }
        }

        throw new IllegalStateException("Cannot determine current user type");
    }

    @Override
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_" + role));
        }
        return false;
    }

    private String extractUserTypeFromAuthorities(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_admin"));
        boolean isRecruiter = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_recruiter"));
        boolean isCandidate = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_candidate"));

        if (isAdmin) return "admin";
        if (isRecruiter) return "recruiter";
        if (isCandidate) return "candidate";
        return null;
    }
}*/


package com.onceClick.recruitmentService.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

// shared/impl/CurrentUserService.java
@Service
public class CurrentUserServiceImpl implements CurrentUser {

    @Override
    public UUID getCurrentAccountId() {
        return UUID.fromString(SecurityContextHolder.getContext()
                .getAuthentication().getName());
    }

    @Override
    public String getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_admin"));

        boolean isRecruiter = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_recruiter"));

        boolean isCandidate = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_candidate"));

        if (isAdmin) return "admin";
        if (isRecruiter) return "recruiter";
        if (isCandidate) return "candidate";

        throw new IllegalStateException("Unsupported user role");
    }

    @Override
    public boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_" + role));
    }
}
