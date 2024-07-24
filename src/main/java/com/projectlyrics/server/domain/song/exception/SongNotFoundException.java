package com.projectlyrics.server.domain.song.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class SongNotFoundException extends FeelinException {

    public SongNotFoundException() {
        super(ErrorCode.SONG_NOT_FOUND);
    }
}
