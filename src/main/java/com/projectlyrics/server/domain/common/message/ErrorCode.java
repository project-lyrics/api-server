package com.projectlyrics.server.domain.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "00400", "Invalid request."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "00500", "An error occurred while processing request."),

  // Artist
  ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "00600", "The artist data could not be found."),
  ARTIST_UPDATE_NOT_VALID(HttpStatus.BAD_REQUEST, "00601", "The data to be updated failed validation."),

  // Auth
  AUTHENTICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "00601", "The authentication code is expired."),
  UNSUPPORTED_AUTH_PROVIDER(HttpStatus.BAD_REQUEST, "00601", "It is unsupported authentication provider"),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "00601", "The refresh token could not be found."),
  WRONG_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "00601", "Wrong token type is passed."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "00401", "The token is not valid."),
  INVALID_KEY(HttpStatus.UNAUTHORIZED, "00401", "The key is not valid."),

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "00600", "The user data could not be found."),
  ;

  private final HttpStatus responseStatus;
  private final String errorCode;
  private final String errorMessage;
}
