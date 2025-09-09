package com.kh.auth.api.dto.response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String name,
        String avatarUrl,
        String provider,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant updatedAt
) {}

