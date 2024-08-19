package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class AlreadyExistsUserException extends FeelinException {

    public AlreadyExistsUserException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }
}
