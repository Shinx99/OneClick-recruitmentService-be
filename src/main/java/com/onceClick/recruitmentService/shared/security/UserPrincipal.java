package com.onceClick.recruitmentService.shared.security;

import java.security.Principal;
import java.util.UUID;

/**
 * Chỉ sử dụng cho ws
 * */
public interface UserPrincipal extends Principal {
    UUID getUserId();
    String getUserType();
}