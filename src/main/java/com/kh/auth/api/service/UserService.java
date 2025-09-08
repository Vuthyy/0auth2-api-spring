package com.kh.auth.api.service;

import com.kh.auth.api.dto.request.RegisterRequestDto;
import com.kh.auth.api.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserResponse registerLocalUser(RegisterRequestDto req);
    UserResponse findOrCreateOAuth2User(String email, String name, String avatarUrl, String providerName);
    UserResponse loadDomainUser(String usernameOrEmail);
}
