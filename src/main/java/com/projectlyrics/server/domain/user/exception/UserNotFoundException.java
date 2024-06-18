package com.projectlyrics.server.domain.user.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class UserNotFoundException extends FeelinException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
