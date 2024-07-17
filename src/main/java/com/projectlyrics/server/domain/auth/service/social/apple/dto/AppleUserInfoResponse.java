package com.projectlyrics.server.domain.auth.service.social.apple.dto;

import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.user.entity.AuthProvider;

public record AppleUserInfoResponse(
        String id
) {
    public AuthSocialInfo toAuthUserInfo() {
        return new AuthSocialInfo(
                AuthProvider.APPLE,
                id
        );
    }
}
