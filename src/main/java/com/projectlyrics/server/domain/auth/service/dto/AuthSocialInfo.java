package com.projectlyrics.server.domain.auth.service.dto;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.user.entity.User;

public record AuthSocialInfo(
        AuthProvider authProvider,
        String socialId,
        String email
) {
    public User toEntity(Role role) {
        return User.of(socialId, email, authProvider, role);
    }
}
