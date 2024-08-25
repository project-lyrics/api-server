package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class AuthNotFoundException extends FeelinException {

    public AuthNotFoundException() {
        super(ErrorCode.AUTH_NOT_FOUND);
    }
}
