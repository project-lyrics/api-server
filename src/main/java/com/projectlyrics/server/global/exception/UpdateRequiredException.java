package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class UpdateRequiredException extends FeelinException {

    public UpdateRequiredException() {
        super(ErrorCode.UPDATE_REQUIRED);
    }
}
