package com.projectlyrics.server.domain.auth.service.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        String id,
        KakaoAccount kakaoAccount
) {
  public UserInfoResponse toUserInfo() {
    return new UserInfoResponse(
        AuthProvider.KAKAO,
        id,
        kakaoAccount.email()
    );
  }
}
