package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class DomainNullFieldException extends FeelinException {

    public DomainNullFieldException() {
        super(ErrorCode.NULL_FIELD);
    }
}
