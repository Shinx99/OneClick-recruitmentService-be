// features/user/service/UserService.java (INTERFACE)
package com.onceClick.recruitmentService.features.user.service;

import java.util.UUID;

public interface UserService {
    UserInfo getUserInfo(UUID userId, String userType);
    void evictUserCache(UUID userId);
    
    record UserInfo(UUID userId, String fullName, String email, String avatarUrl, String userType) {}
}