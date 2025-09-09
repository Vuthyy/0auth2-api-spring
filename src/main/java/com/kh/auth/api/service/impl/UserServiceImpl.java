package com.kh.auth.api.service.impl;

import com.kh.auth.api.dto.request.RegisterRequestDto;
import com.kh.auth.api.dto.response.UserResponse;
import com.kh.auth.api.entity.UserEntity;
import com.kh.auth.api.enums.AuthProvider;
import com.kh.auth.api.enums.Role;
import com.kh.auth.api.exception.AppException;
import com.kh.auth.api.mapper.UserMapper;
import com.kh.auth.api.repository.UserRepository;
import com.kh.auth.api.service.UserService;
import com.kh.auth.api.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final PasswordUtils passwordUtils;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        var user = repo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        // This part is correct as it maps to a Spring Security User
        return User
                .withUsername(user.getUsername())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .accountLocked(false).disabled(false).build();
    }

    @Override
    public UserResponse loadDomainUser(String usernameOrEmail) {
        UserEntity userEntity = repo.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new AppException("User not found"));
        // Map from Entity to Response DTO
        return userMapper.toResponse(userEntity);
    }

    @Override
    @Transactional
    public UserResponse registerLocalUser(RegisterRequestDto req) {
        if (repo.existsByUsername(req.getUsername()))
            throw new AppException("Username already exists");
        if (repo.existsByEmail(req.getEmail()))
            throw new AppException("Email already exists");

        // Use UserEntity.builder(), not User.builder()
        var newUserEntity = UserEntity.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordUtils.encode(req.getPassword()))
                .provider(AuthProvider.LOCAL)
                .role(Role.USER)
                .name(req.getName())
                .build();

        var savedUser = repo.save(newUserEntity);

        // Map the saved entity to the response DTO
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse findOrCreateOAuth2User(String email, String name, String avatarUrl, String providerName) {
        if (email == null || email.isBlank()) {
            throw new AppException("Provider did not return an email; cannot create account.");
        }

        // Find user, if present map to response. If not, create, save, and then map.
        return repo.findByEmail(email)
                .map(userMapper::toResponse) // If found, map to response
                .orElseGet(() -> { // If not found, create a new one
                    var usernamePart = email.split("@")[0];
                    String candidate = usernamePart;
                    int i = 1;
                    while (repo.existsByUsername(candidate)) {
                        candidate = usernamePart + i++;
                    }

                    // Use UserEntity.builder() here as well
                    var newUserEntity = UserEntity.builder()
                            .username(candidate)
                            .email(email)
                            .password(null)
                            .provider(AuthProvider.valueOf(providerName.toUpperCase()))
                            .role(Role.USER)
                            .name(name)
                            .avatarUrl(avatarUrl)
                            .build();

                    var savedUser = repo.save(newUserEntity);
                    return userMapper.toResponse(savedUser);
                });
    }
}