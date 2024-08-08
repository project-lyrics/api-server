package com.projectlyrics.server.domain.note.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class NoteNotFoundException extends FeelinException {

    public NoteNotFoundException() {
        super(ErrorCode.NOTE_NOT_FOUND);
    }
}
