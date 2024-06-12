package com.projectlyrics.server.global.exception;

import lombok.Getter;
import com.projectlyrics.server.domain.common.message.ErrorCode;

@Getter
public class FeelinException extends RuntimeException {

  private final ErrorCode errorCode;

  public FeelinException(ErrorCode errorCode) {
    super(errorCode.getErrorMessage());
    this.errorCode = errorCode;
  }

  public FeelinException(ErrorCode errorCode, String messageForLog) {
    super(messageForLog);
    this.errorCode = errorCode;
  }

  public FeelinException(ErrorCode errorCode, Exception originalException) {
    super(originalException);
    this.errorCode = errorCode;
  }

  public FeelinException(ErrorCode errorCode, String messageForLog, Exception originalException) {
    super(messageForLog, originalException);
    this.errorCode = errorCode;
  }
}
