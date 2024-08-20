package com.projectlyrics.server.domain.auth.domain;

import com.projectlyrics.server.domain.auth.dto.request.AuthSignInRequest;
import com.projectlyrics.server.domain.auth.dto.request.AuthSignUpRequest;
import com.projectlyrics.server.domain.user.entity.AuthProvider;

public record AuthGetSocialInfo(
        String socialAccessToken,
        AuthProvider authProvider
) {

    public static AuthGetSocialInfo from(AuthSignInRequest request) {
        return new AuthGetSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );
    }

    public static AuthGetSocialInfo from(AuthSignUpRequest request) {
        return new AuthGetSocialInfo(
                request.socialAccessToken(),
                request.authProvider()
        );
    }
}
