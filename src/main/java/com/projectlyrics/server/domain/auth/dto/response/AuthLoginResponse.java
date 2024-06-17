package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.entity.enumerate.Role;
import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;

public record AuthLoginResponse(
    Role role,
    String accessToken,
    String refreshToken,
    boolean isRegistered
) {
  public static AuthLoginResponse of(Role role, AuthToken authToken, boolean isRegistered) {
    return new AuthLoginResponse(
        role,
        authToken.accessToken(),
        authToken.refreshToken(),
        isRegistered
    );
  }
}
