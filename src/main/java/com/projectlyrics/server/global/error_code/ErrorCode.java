package com.projectlyrics.server.global.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "00400", "Invalid request."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "00500", "An error occurred while processing request.");

  private final HttpStatus responseStatus;
  private final String errorCode;
  private final String errorMessage;
}
