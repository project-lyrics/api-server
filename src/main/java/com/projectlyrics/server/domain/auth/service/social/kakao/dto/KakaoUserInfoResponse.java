package com.projectlyrics.server.domain.auth.service.social.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.projectlyrics.server.domain.auth.service.dto.AuthSocialInfo;
import com.projectlyrics.server.domain.auth.entity.enumerate.AuthProvider;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        String id,
        KakaoAccount kakaoAccount
) {
  public AuthSocialInfo toKakaoUserInfo() {
    return new AuthSocialInfo(
        AuthProvider.KAKAO,
        id,
        kakaoAccount.email()
    );
  }
}
