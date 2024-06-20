package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;

public record AuthLoginResponse(
        String accessToken,
        String refreshToken
) {
    public static AuthLoginResponse of(AuthToken authToken) {
        return new AuthLoginResponse(
                authToken.accessToken(),
                authToken.refreshToken()
        );
    }
}
