package com.projectlyrics.server.domain.auth.service.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoAccessTokenResponse(
    String accessToken
) {

  public static KakaoAccessTokenResponse of(
      String accessToken
  ) {
    return new KakaoAccessTokenResponse(accessToken);
  }
}
