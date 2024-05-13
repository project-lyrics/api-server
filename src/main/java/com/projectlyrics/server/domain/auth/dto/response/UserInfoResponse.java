package com.projectlyrics.server.domain.auth.dto.response;

import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;
import com.projectlyrics.server.domain.user.entity.User;

public record UserInfoResponse(
    AuthProvider authProvider,
    Long socialId,
    String email
) {
  public User toEntity() {
    return User.of(socialId, email, authProvider);
  }
}
