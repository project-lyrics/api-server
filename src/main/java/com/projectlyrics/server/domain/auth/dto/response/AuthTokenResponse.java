package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        Long userId
) {

    public static AuthTokenResponse of(AuthToken authToken, Long userId) {
        return new AuthTokenResponse(
                authToken.accessToken(),
                authToken.refreshToken(),
                userId
        );
    }
}
