package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import lombok.Getter;

@Getter
public class FeelinException extends RuntimeException {

  private final ErrorCode errorCode;

  public FeelinException(ErrorCode errorCode) {
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
