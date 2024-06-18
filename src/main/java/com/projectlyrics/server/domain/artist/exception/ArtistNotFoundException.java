package com.projectlyrics.server.domain.artist.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class ArtistNotFoundException extends FeelinException {

    public ArtistNotFoundException() {
        super(ErrorCode.ARTIST_NOT_FOUND);
    }
}
