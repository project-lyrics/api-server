package com.projectlyrics.server.domain.user.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidProfileCharacterException extends FeelinException {

    public InvalidProfileCharacterException() {
        super(ErrorCode.INVALID_PROFILE_CHARACTER);
    }
}
