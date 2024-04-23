package com.projectlyrics.server.domain.auth.external.dto.response;

import com.projectlyrics.server.domain.user.entity.User;
import com.projectlyrics.server.domain.auth.external.AuthProvider;

public record UserInfoResponse(
    Long socialId,
    AuthProvider authProvider,
    String email
) {
  public User toEntity() {
    return User.of(socialId, email, authProvider);
  }
}
