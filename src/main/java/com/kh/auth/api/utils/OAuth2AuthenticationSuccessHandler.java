package com.kh.auth.api.utils;

import com.kh.auth.api.service.JwtService;
import com.kh.auth.api.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = (String) oAuth2User.getAttributes().get("username");
        var user = userService.loadDomainUser(username);

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri == null || redirectUri.isBlank()) redirectUri = "http://localhost:3000/oauth2/callback";

        String url = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", access)
                .queryParam("refreshToken", refresh)
                .build().toUriString();

        Cookie refreshCookie = new Cookie("refresh_token", refresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        response.sendRedirect(url);
    }
}
