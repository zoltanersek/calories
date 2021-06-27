package com.zoltan.calories.user;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    ROLE_USER,
    ROLE_ADMIN,
    ROLE_USER_MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
}
