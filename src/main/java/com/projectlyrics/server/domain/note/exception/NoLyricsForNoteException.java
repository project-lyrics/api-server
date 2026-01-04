package com.projectlyrics.server.domain.note.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class NoLyricsForNoteException extends FeelinException {

    public NoLyricsForNoteException() {
        super(ErrorCode.NO_LYRICS_FOR_NOTE);
    }
}
