package com.projectlyrics.server.domain.auth.service.apple.dto;

import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;

public record AppleUserInfoResponse(
    String id
) {
  public UserInfoResponse toUserInfo() {
    return new UserInfoResponse(
        AuthProvider.APPLE,
        id,
        ""
    );
  }
}
