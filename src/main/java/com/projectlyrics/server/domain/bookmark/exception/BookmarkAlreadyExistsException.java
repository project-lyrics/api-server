package com.projectlyrics.server.domain.bookmark.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class BookmarkAlreadyExistsException extends FeelinException {

    public BookmarkAlreadyExistsException() {
        super(ErrorCode.BOOKMARK_ALREADY_EXISTS);
    }
}
