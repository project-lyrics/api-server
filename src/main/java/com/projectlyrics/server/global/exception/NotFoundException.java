package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.global.message.ErrorCode;

public class NotFoundException extends BusinessException {

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

  public NotFoundException(ErrorCode errorCode, String messageForLog) {
    super(errorCode, messageForLog);
  }

  public NotFoundException(ErrorCode errorCode, String messageForLog, Exception originalException) {
    super(errorCode, messageForLog, originalException);
  }

  public NotFoundException(ErrorCode errorCode, Exception originalException) {
    super(errorCode, originalException);
  }
}
