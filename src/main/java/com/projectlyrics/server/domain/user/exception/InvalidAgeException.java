package com.projectlyrics.server.domain.user.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidAgeException extends FeelinException {
    public InvalidAgeException() {
        super(ErrorCode.INVALID_AGE);
    }
}
