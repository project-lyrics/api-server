package com.projectlyrics.server.global.auth.external.kakao.dto;

public record KakaoAccessTokenResponse(
    String accessToken
) {

  public static KakaoAccessTokenResponse of(
      String accessToken
  ) {
    return new KakaoAccessTokenResponse(accessToken);
  }
}
