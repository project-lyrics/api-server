package com.projectlyrics.server.domain.note.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidNoteBackgroundException extends FeelinException {

    public InvalidNoteBackgroundException() {
        super(ErrorCode.INVALID_NOTE_BACKGROUND);
    }
}
