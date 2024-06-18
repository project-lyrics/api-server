package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class AccessTokenExpiredException extends FeelinException {

    public AccessTokenExpiredException() {
        super(ErrorCode.ACCESS_TOKEN_EXPIRED);
    }
}
