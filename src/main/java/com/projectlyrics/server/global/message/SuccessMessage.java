package com.projectlyrics.server.global.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

  LOGIN_SUCCESS("소셜 로그인이 완료되었습니다."),
  ;

  private final String message;
}
