package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class JwtExpiredException extends FeelinException {

  public JwtExpiredException(ErrorCode errorCode) {
    super(errorCode);
  }

  public JwtExpiredException(ErrorCode errorCode, String messageForLog) {
    super(errorCode, messageForLog);
  }

  public JwtExpiredException(ErrorCode errorCode, Exception originalException) {
    super(errorCode, originalException);
  }

  public JwtExpiredException(ErrorCode errorCode, String messageForLog,
      Exception originalException) {
    super(errorCode, messageForLog, originalException);
  }
}
