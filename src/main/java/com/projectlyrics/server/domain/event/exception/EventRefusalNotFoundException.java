package com.projectlyrics.server.domain.event.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class EventRefusalNotFoundException extends FeelinException {

    public EventRefusalNotFoundException() {
        super(ErrorCode.EVENT_REFUSAL_NOT_FOUND);
    }
}
