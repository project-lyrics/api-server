package com.projectlyrics.server.domain.favoriteartist.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FavoriteArtistAlreadyExistsException extends FeelinException {

    public FavoriteArtistAlreadyExistsException() {
        super(ErrorCode.FAVORITE_ARTIST_ALREADY_EXISTS);
    }
}
