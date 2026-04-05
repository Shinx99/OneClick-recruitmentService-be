package com.onceClick.recruitmentService.shared.security;

import java.util.UUID;

// shared/UserContext.java (interface)
public interface CurrentUser {
    UUID getCurrentAccountId();
    String getCurrentUserType();
    boolean hasRole(String role);
}
