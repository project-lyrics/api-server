package com.projectlyrics.server.domain.comment.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidCommentUpdateException extends FeelinException {

    public InvalidCommentUpdateException() {
        super(ErrorCode.INVALID_NOTE_UPDATE);
    }
}
