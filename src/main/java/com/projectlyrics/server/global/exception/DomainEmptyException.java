package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class DomainEmptyException extends FeelinException {

    public DomainEmptyException() {
        super(ErrorCode.EMPTY_FIELD);
    }
}
