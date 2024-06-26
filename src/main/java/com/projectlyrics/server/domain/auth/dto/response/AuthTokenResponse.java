package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken
) {
    public static AuthTokenResponse of(AuthToken authToken) {
        return new AuthTokenResponse(
                authToken.accessToken(),
                authToken.refreshToken()
        );
    }
}
