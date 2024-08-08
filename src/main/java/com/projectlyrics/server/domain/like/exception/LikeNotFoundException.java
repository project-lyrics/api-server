package com.projectlyrics.server.domain.like.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class LikeNotFoundException extends FeelinException {

    public LikeNotFoundException() {
        super(ErrorCode.LIKE_NOT_FOUND);
    }
}
