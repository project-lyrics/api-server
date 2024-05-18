package com.projectlyrics.server.global.handler;

import com.projectlyrics.server.domain.common.dto.ErrorResponse;
import com.projectlyrics.server.global.exception.JwtValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JwtValidationException.class)
  public ResponseEntity<ErrorResponse> handleJwtValidationException(JwtValidationException e) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of(e.getErrorCode()));
  }
}
