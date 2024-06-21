package com.projectlyrics.server.domain.auth.service.dto;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;

public record AuthSocialInfo(
        AuthProvider authProvider,
        String socialId,
        String email
) {
}
