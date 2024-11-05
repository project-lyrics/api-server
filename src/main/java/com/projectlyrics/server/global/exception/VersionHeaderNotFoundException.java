package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;

public class VersionHeaderNotFoundException extends FeelinException {

    public VersionHeaderNotFoundException() {
        super(ErrorCode.VERSION_HEADER_NOT_FOUND);
    }
}
