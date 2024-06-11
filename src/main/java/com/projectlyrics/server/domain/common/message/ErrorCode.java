package com.projectlyrics.server.domain.common.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // Common
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "00400", "Invalid request."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "00401", "Invalid input value is passed."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "00500", "An unexpected error occurred on the server."),

  // Artist
  ARTIST_UPDATE_NOT_VALID(HttpStatus.BAD_REQUEST, "00400", "The data to be updated failed validation."),
  ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "00404", "The artist data could not be found."),

  // Auth
  ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "00400", "The token is expired."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "00400", "The refresh token could not be found."),
  WRONG_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "00400", "Wrong token type is passed."),
  UNSUPPORTED_AUTH_PROVIDER(HttpStatus.BAD_REQUEST, "00400", "It is unsupported authentication provider"),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "00401", "The token is not valid."),
  INVALID_KEY(HttpStatus.UNAUTHORIZED, "00401", "The key is not valid."),

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "00404", "The user data could not be found."),

  // Record
  RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "00404", "The record data could not be found."),
  ;

  private final HttpStatus responseStatus;
  private final String errorCode;
  private final String errorMessage;
}
