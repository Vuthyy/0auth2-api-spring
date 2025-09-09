package com.kh.auth.api.service;

import com.kh.auth.api.dto.response.UserResponse;

public interface JwtService {
    String generateAccessToken(UserResponse user);
    String generateRefreshToken(UserResponse user);
    UserResponse parseAccessToken(String token);
    UserResponse parseRefreshToken(String token);
}
