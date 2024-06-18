package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidAdminKeyException extends FeelinException {

    public InvalidAdminKeyException() {
        super(ErrorCode.INVALID_KEY);
    }
}
