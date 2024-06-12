package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.jwt.dto.AuthToken;

public record UserTokenReissueResponse(
    String accessToken,
    String refreshToken
) {
  public static UserTokenReissueResponse from(AuthToken authToken) {
    return new UserTokenReissueResponse(authToken.accessToken(), authToken.refreshToken());
  }
}
