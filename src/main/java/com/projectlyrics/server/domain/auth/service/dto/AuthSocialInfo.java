package com.projectlyrics.server.domain.auth.service.dto;

import com.projectlyrics.server.domain.user.entity.AuthProvider;

public record AuthSocialInfo(
        AuthProvider authProvider,
        String socialId
) {
}
