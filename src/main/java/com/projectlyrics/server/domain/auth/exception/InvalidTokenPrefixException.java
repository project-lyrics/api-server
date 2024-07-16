package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidTokenPrefixException extends FeelinException {
    public InvalidTokenPrefixException() {
        super(ErrorCode.INVALID_TOKEN_PREFIX);
    }
}
