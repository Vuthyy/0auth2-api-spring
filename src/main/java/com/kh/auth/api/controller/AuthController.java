package com.kh.auth.api.controller;

import com.kh.auth.api.dto.request.LoginRequestDto;
import com.kh.auth.api.dto.request.RegisterRequestDto;
import com.kh.auth.api.dto.response.AuthResponseDto;
import com.kh.auth.api.dto.response.UserResponseDto;
import com.kh.auth.api.mapper.UserMapper;
import com.kh.auth.api.service.JwtService;
import com.kh.auth.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody RegisterRequestDto req) {
        var user = userService.registerLocalUser(req);
        return ResponseEntity.ok(userMapper.to(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsernameOrEmail(), req.getPassword()));
        var user = userService.loadDomainUser(auth.getName());
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok(new AuthResponseDto(access, refresh));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        var user = jwtService.parseRefreshToken(refreshToken);
        String access = jwtService.generateAccessToken(user);
        String newRefresh = jwtService.generateRefreshToken(user);
        return ResponseEntity.ok(new AuthResponseDto(access, newRefresh));
    }
}
