package com.kh.auth.api.service.impl;

import com.kh.auth.api.dto.request.UserRequest;
import com.kh.auth.api.dto.response.UserResponse;
import com.kh.auth.api.repository.UserRepository;
import com.kh.auth.api.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final UserRepository userRepository;

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
    public String generateAccessToken(UserRequest user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessMinutes * 60)))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserRequest user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshDays * 24 * 3600)))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public UserResponse parseAccessToken(String token) {
        var claims = parse(token);
        var username = claims.getSubject();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public UserResponse parseRefreshToken(String token) {
        var claims = parse(token);
        if (!"refresh".equals(claims.get("type"))) throw new JwtException("Not a refresh token");
        var username = claims.getSubject();
        return userRepository.findByUsername(username).orElseThrow();
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload();
    }
}


