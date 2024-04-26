package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.global.message.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, String messageForLog) {
    super(messageForLog);
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, Exception originalException) {
    super(originalException);
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, String messageForLog, Exception originalException) {
    super(messageForLog, originalException);
    this.errorCode = errorCode;
  }
}
