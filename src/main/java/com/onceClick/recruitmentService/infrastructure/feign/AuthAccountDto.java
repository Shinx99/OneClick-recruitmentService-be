package com.onceClick.recruitmentService.infrastructure.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthAccountDto {

    private UUID accountId;
    private String email;
    private String phone;
    private String status;
    private Set<String> roles;

    public boolean hasRole(String roleName) {
        return roles != null && roles.contains(roleName);
    }

}