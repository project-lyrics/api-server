package com.projectlyrics.server.domain.auth.service.dto;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.auth.entity.util.AuthBuilder;
import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.user.entity.util.UserBuilder;

public record AuthSocialInfo(
        AuthProvider authProvider,
        String socialId,
        String email
) {
    public User toEntity(Role role) {
        return UserBuilder.build(
                AuthBuilder.build(authProvider, role, socialId),
                email
        );
    }
}
