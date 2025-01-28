package com.projectlyrics.server.global.exception;

import lombok.Getter;

import com.projectlyrics.server.domain.common.message.ErrorCode;

@Getter
public class FeelinException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object data;

    public FeelinException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.data = null;
    }

    public FeelinException(ErrorCode errorCode, String messageForLog) {
        super(messageForLog);
        this.errorCode = errorCode;
        this.data = null;
    }

    public FeelinException(ErrorCode errorCode, Exception originalException) {
        super(originalException);
        this.errorCode = errorCode;
        this.data = null;
    }

    public FeelinException(ErrorCode errorCode, String messageForLog, Exception originalException) {
        super(messageForLog, originalException);
        this.errorCode = errorCode;
        this.data = null;
    }

    public FeelinException(ErrorCode errorCode, Object data) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
        this.data = data;
    }

    public FeelinException(ErrorCode errorCode, String messageForLog, Object data) {
        super(messageForLog);
        this.errorCode = errorCode;
        this.data = data;
    }

    public FeelinException(ErrorCode errorCode, String messageForLog, Exception originalException, Object data) {
        super(messageForLog, originalException);
        this.errorCode = errorCode;
        this.data = data;
    }
}
