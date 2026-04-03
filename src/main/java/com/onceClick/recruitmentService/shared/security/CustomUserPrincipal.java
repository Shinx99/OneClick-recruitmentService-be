package com.onceClick.recruitmentService.shared.security;

import java.security.Principal;
import java.util.UUID;

public class CustomUserPrincipal implements UserPrincipal {
    private final UUID userId;
    private final String userType;
    private final String name;
    
    public CustomUserPrincipal(UUID userId, String userType) {
        this.userId = userId;
        this.userType = userType;
        this.name = userId.toString();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public String getUserType() {
        return userType;
    }
}