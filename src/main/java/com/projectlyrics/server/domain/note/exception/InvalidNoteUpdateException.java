package com.projectlyrics.server.domain.note.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidNoteUpdateException extends FeelinException {

    public InvalidNoteUpdateException() {
        super(ErrorCode.INVALID_NOTE_UPDATE);
    }
}
