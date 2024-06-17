package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class AuthException extends FeelinException {

  public AuthException(ErrorCode errorCode) {
    super(errorCode);
  }

  public AuthException(ErrorCode errorCode, String messageForLog) {
    super(errorCode, messageForLog);
  }

  public AuthException(ErrorCode errorCode, Exception originalException) {
    super(errorCode, originalException);
  }

  public AuthException(ErrorCode errorCode, String messageForLog, Exception originalException) {
    super(errorCode, messageForLog, originalException);
  }
}
