package com.kh.auth.api.service;

import com.kh.auth.api.dto.request.UserRequest;
import com.kh.auth.api.dto.response.UserResponse;

public interface JwtService {
    String generateAccessToken(UserRequest user);
    String generateRefreshToken(UserRequest user);
    UserResponse parseAccessToken(String token);
    UserResponse parseRefreshToken(String token);
}
