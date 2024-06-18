package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class JwtValidationException extends FeelinException {

    public JwtValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtValidationException(ErrorCode errorCode, String messageForLog) {
        super(errorCode, messageForLog);
    }

    public JwtValidationException(ErrorCode errorCode, String messageForLog, Exception originalException) {
        super(errorCode, messageForLog, originalException);
    }

    public JwtValidationException(ErrorCode errorCode, Exception originalException) {
        super(errorCode, originalException);
    }
}
