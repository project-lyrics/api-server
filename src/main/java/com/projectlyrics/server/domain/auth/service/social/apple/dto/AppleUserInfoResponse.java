package com.projectlyrics.server.domain.auth.service.social.apple.dto;

import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;

public record AppleUserInfoResponse(
    String id
) {
  public AuthSocialInfo toUserInfo() {
    return new AuthSocialInfo(
        AuthProvider.APPLE,
        id,
        ""
    );
  }
}
