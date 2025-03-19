package com.projectlyrics.server.domain.banner.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class BannerNotFoundException extends FeelinException {
    public BannerNotFoundException() { super(ErrorCode.BANNER_NOT_FOUND);}
}
