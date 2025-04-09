package com.projectlyrics.server.domain.view.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class ViewNotFoundException extends FeelinException {
    public ViewNotFoundException() {super(ErrorCode.VIEW_NOT_FOUND);}
}
