package com.projectlyrics.server.domain.like.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class LikeAlreadyExistsException extends FeelinException {

    public LikeAlreadyExistsException() {
        super(ErrorCode.LIKE_ALREADY_EXISTS);
    }
}
