package com.onceClick.recruitmentService.infrastructure.feign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthAccountDto {

    private UUID accountId;
    private String email;
    private String phone;
    private String status;
    private String role;

}