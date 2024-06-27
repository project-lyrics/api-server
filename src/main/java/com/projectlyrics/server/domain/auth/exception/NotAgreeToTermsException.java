package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class NotAgreeToTermsException extends FeelinException {

    public NotAgreeToTermsException() {
        super(ErrorCode.NOT_AGREE_TO_TERMS);
    }
}
