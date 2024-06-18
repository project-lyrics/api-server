package com.projectlyrics.server.domain.auth.jwt.dto;

public record AuthToken(
        String accessToken,
        String refreshToken
) {

}
