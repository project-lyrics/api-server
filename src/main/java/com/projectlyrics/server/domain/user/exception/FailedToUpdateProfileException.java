package com.projectlyrics.server.domain.user.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FailedToUpdateProfileException extends FeelinException {

    public FailedToUpdateProfileException() {
        super(ErrorCode.FAILED_TO_UPDATE_PROFILE);
    }
}
