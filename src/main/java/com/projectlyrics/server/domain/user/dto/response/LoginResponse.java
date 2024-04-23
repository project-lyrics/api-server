package com.projectlyrics.server.domain.user.dto.response;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {
}
