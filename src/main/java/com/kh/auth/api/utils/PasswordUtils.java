package com.kh.auth.api.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public String encode(String raw) {
        return encoder.encode(raw);
    }
    public boolean matches(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }
}
