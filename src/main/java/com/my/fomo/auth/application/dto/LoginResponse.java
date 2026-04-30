package com.my.fomo.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private final UserResponse user;
    private final String token;
    private final String refreshToken;
}
