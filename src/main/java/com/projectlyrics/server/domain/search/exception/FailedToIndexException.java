package com.projectlyrics.server.domain.search.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FailedToIndexException extends FeelinException {

    public FailedToIndexException() {
        super(ErrorCode.FAILED_TO_INDEX);
    }
}
