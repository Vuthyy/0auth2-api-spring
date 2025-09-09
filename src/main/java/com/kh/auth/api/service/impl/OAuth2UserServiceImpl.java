package com.kh.auth.api.service.impl;

import com.kh.auth.api.converter.OAuth2AttributeConverter;
import com.kh.auth.api.enums.AuthProvider;
import com.kh.auth.api.service.OAuth2UserService;
import com.kh.auth.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        var delegate = new DefaultOAuth2UserService();
        var user = delegate.loadUser(req);

        String registrationId = req.getClientRegistration().getRegistrationId().toUpperCase();
        var provider = AuthProvider.valueOf(registrationId);
        Map<String, Object> attrs = user.getAttributes();

        var profile = OAuth2AttributeConverter.from(provider, attrs);
        var domainUser = userService.findOrCreateOAuth2User(profile.email(), profile.name(), profile.avatarUrl(), registrationId);

        // expose username as principal name
        return new DefaultOAuth2User(
                List.of(() -> "ROLE_" + domainUser.getRole().name()),
                Map.of("username", domainUser.getUsername(),
                        "email", domainUser.getEmail(),
                        "name", domainUser.getName()),
                "username"
        );
    }
}


