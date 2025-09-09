package com.kh.auth.api.service.impl;

import com.kh.auth.api.repository.UserRepository;
import com.kh.auth.api.dto.response.UserResponse;
import com.kh.auth.api.entity.UserEntity;
import com.kh.auth.api.mapper.UserMapper; // Import the mapper
import com.kh.auth.api.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit; // Use ChronoUnit for clarity
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;
    private final UserMapper userMapper; // 1. Inject the mapper

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-exp-minutes:15}")
    private long accessMinutes;

    @Value("${app.jwt.refresh-exp-days:30}")
    private long refreshDays;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public String generateAccessToken(UserResponse user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername()) // Correct modern API call
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                // Use ChronoUnit for better readability
                .expiration(Date.from(now.plus(accessMinutes, ChronoUnit.MINUTES)))
                .signWith(key()) // Correct modern API call
                .compact();
    }

    @Override
    public String generateRefreshToken(UserResponse user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername()) // Correct modern API call
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshDays, ChronoUnit.DAYS)))
                .signWith(key()) // Correct modern API call
                .compact();
    }

    @Override
    public UserResponse parseAccessToken(String token) {
        Claims claims = parse(token);
        String username = claims.getSubject();
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new JwtException("User not found"));
        // 2. Use the mapper to convert Entity to DTO
        return userMapper.toResponse(userEntity);
    }

    @Override
    public UserResponse parseRefreshToken(String token) {
        Claims claims = parse(token);
        if (!"refresh".equals(claims.get("type"))) {
            throw new JwtException("Not a refresh token");
        }
        String username = claims.getSubject();
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new JwtException("User not found"));
        // 3. Use the mapper here as well
        return userMapper.toResponse(userEntity);
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key()) // Correct modern API call
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}