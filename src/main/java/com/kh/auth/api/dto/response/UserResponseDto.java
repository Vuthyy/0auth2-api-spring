package com.kh.auth.api.dto.response;

import java.time.Instant;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String name,
        String avatarUrl,
        String provider,
        Instant createdAt,
        Instant updatedAt
) {}
