package com.projectlyrics.server.domain.search.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FailedToSearchException extends FeelinException {

    public FailedToSearchException() {
        super(ErrorCode.FAILED_TO_SEARCH);
    }
}
