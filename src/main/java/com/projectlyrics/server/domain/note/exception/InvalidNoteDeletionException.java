package com.projectlyrics.server.domain.note.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidNoteDeletionException extends FeelinException {

    public InvalidNoteDeletionException() {
        super(ErrorCode.INVALID_NOTE_DELETION);
    }
}
