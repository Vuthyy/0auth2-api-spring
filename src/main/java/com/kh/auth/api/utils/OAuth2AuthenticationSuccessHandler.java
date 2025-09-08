package com.kh.auth.api.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        var oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = (String) oAuth2User.getAttributes().get("username");
        var user = userService.loadDomainUser(username);

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        // send tokens to frontend — example: redirect with tokens in fragment (or set cookies)
        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri == null || redirectUri.isBlank()) redirectUri = "http://localhost:3000/oauth2/callback";

        String url = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", access)
                .queryParam("refreshToken", refresh)
                .build().toUriString();

        // or set httpOnly cookies (choose one strategy)
        Cookie refreshCookie = new Cookie("refresh_token", refresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        response.sendRedirect(url);
    }
}