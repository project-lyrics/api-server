package com.projectlyrics.server.domain.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

  // Auth
  LOGIN_SUCCESS("소셜 로그인이 완료되었습니다."),
  TOKEN_VALID_SUCCESS("액세스 토큰이 유효합니다."),
  TOKEN_REISSUE_SUCCESS("액세스 토큰이 재발급되었습니다."),

  // Artist
  ARTIST_ADD_SUCCESS("아티스트가 추가되었습니다."),
  ARTIST_UPDATE_SUCCESS("아티스트가 수정되었습니다."),
  ARTIST_DELETE_SUCCESS("아티스트가 삭제되었습니다."),
  ARTIST_GET_SUCCESS("아티스트 정보를 조회하였습니다."),
  ARTIST_GET_LIST_SUCCESS("아티스트 목록을 조회하였습니다."),
  ARTIST_GET_LIST_BY_NAME_SUCCESS("아티스트 목록을 사용자 검색어 기반으로 조회하였습니다."),
  ;

  private final String message;
}
