package com.projectlyrics.server.domain.comment.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidCommentDeletionException extends FeelinException {

    public InvalidCommentDeletionException() {
        super(ErrorCode.INVALID_COMMENT_DELETION);
    }
}
