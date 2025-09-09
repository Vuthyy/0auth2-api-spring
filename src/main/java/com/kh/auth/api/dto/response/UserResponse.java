package com.kh.auth.api.dto.response;

import com.kh.auth.api.entity.BaseEntity;
import com.kh.auth.api.enums.AuthProvider;
import com.kh.auth.api.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserResponse extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private AuthProvider provider;
    private Role role;
    private String name;
    private String avatarUrl;
}
