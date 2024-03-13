package com.projectlyrics.server.global.error_code;

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
  ARTIST_UPDATE_NOT_VALID(HttpStatus.BAD_REQUEST, "00601", "The data to be updated failed validation.")
  ;

  private final HttpStatus responseStatus;
  private final String errorCode;
  private final String errorMessage;
}
