package com.projectlyrics.server.domain.note.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class TooManyDraftsException extends FeelinException {

    public TooManyDraftsException() {
        super(ErrorCode.TOO_MANY_DRAFT_NOTE);
    }
}
