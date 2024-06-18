package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class WrongTokenTypeException extends FeelinException {

    public WrongTokenTypeException() {
        super(ErrorCode.WRONG_TOKEN_TYPE);
    }
}
