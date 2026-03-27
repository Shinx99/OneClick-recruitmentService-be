package com.onceClick.recruitmentService.shared.security;

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
}