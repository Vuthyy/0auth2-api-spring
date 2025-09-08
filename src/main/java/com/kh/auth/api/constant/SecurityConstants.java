package com.kh.auth.api.constant;

public class SecurityConstants {
    private SecurityConstants() {
    }
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTH_HEADER = "Authorization";
    public static final String[] OAUTH2_WHITELIST = {"/oauth2/**", "/login/oauth2/**"};
}
