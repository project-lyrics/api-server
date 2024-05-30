package com.projectlyrics.server.domain.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

  LOGIN_SUCCESS("소셜 로그인이 완료되었습니다."),
  TOKEN_VALID_SUCCESS("액세스 토큰이 유효합니다."),
  TOKEN_REISSUE_SUCCESS("액세스 토큰이 재발급되었습니다.")
  ;

  private final String message;
}
