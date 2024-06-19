package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class DomainInvalidUrlException extends FeelinException {

    public DomainInvalidUrlException() {
        super(ErrorCode.INVALID_URL_PREFIX);
    }
}
