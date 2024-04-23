package com.projectlyrics.server.domain.user.dto.response;

import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {
  public static LoginResponse of(AuthToken authToken) {
    return new LoginResponse(
        authToken.accessToken(),
        authToken.refreshToken()
    );
  }
}
