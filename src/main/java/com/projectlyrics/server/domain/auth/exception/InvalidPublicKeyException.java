package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidPublicKeyException extends FeelinException {

    public InvalidPublicKeyException() {
        super(ErrorCode.INVALID_KEY);
    }
}
