package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.authentication.jwt.AuthToken;

public record AuthTokenReissueResponse(
        String accessToken,
        String refreshToken
) {
    public static AuthTokenReissueResponse from(AuthToken authToken) {
        return new AuthTokenReissueResponse(authToken.accessToken(), authToken.refreshToken());
    }
}
