package com.projectlyrics.server.domain.auth.authentication.jwt;

public record AuthToken(
        String accessToken,
        String refreshToken
) {

}
