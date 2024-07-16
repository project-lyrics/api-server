package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class NoTokenProvidedException extends FeelinException {

    public NoTokenProvidedException() {
        super(ErrorCode.NO_TOKEN_PROVIDED);
    }
}
