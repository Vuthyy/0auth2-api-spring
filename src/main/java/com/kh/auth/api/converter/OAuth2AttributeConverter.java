package com.kh.auth.api.converter;

import com.kh.auth.api.enums.AuthProvider;

import java.util.Map;

public class OAuth2AttributeConverter {

    // Normalize OAuth2 provider attributes into a minimal profile
    public record Profile(String email, String name, String avatarUrl) {}

    public static Profile from(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new Profile(
                    (String) attributes.get("email"),
                    (String) attributes.getOrDefault("name", attributes.get("given_name")),
                    (String) attributes.get("picture"));
            case GITHUB -> new Profile(
                    (String) attributes.get("email"), // may be null unless scope "user:email"
                    (String) attributes.getOrDefault("name", attributes.get("login")),
                    (String) attributes.get("avatar_url"));
            case FACEBOOK -> new Profile(
                    (String) attributes.get("email"),
                    (String) attributes.getOrDefault("name", "Facebook User"),
                    // Facebook graph v12: picture is nested
                    attributes.containsKey("picture")
                            ? (String) ((Map<?, ?>)((Map<?, ?>)attributes.get("picture")).get("data")).get("url")
                            : null
            );
            case LOCAL -> throw new IllegalArgumentException("LOCAL not OAuth2");
        };
    }
}
