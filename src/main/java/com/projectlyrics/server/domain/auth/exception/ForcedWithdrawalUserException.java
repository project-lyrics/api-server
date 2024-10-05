package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class ForcedWithdrawalUserException extends FeelinException {

    public ForcedWithdrawalUserException() {
        super(ErrorCode.USER_FORCED_WITHDRAWAL);
    }
}

