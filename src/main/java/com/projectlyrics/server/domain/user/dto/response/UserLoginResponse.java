package com.projectlyrics.server.domain.user.dto.response;

import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;

public record UserLoginResponse(
    String accessToken,
    String refreshToken
) {
  public static UserLoginResponse of(AuthToken authToken) {
    return new UserLoginResponse(
        authToken.accessToken(),
        authToken.refreshToken()
    );
  }
}
