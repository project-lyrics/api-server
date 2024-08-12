package com.projectlyrics.server.domain.favoriteartist.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FavoriteArtistNotFoundException extends FeelinException {

    public FavoriteArtistNotFoundException() {
        super(ErrorCode.FAVORITE_ARTIST_NOT_FOUND);
    }
}
