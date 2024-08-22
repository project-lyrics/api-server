package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken
) {
    public static AuthTokenResponse from(AuthToken authToken) {
        return new AuthTokenResponse(
                authToken.accessToken(),
                authToken.refreshToken()
        );
    }
}
