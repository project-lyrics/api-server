package com.projectlyrics.server.global.auth.external.dto.response;

import com.projectlyrics.server.global.auth.external.AuthProvider;

public record UserInfoResponse(
    Long socialId,
    AuthProvider authProvider,
    String email
) {
  public static UserInfoResponse of(
          Long socialId,
          AuthProvider authProvider,
          String email
  ) {
    return new UserInfoResponse(socialId, authProvider, email);
  }
}
