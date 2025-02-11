package com.projectlyrics.server.domain.event.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class EventReceiptNotFoundException extends FeelinException {

    public EventReceiptNotFoundException() {
        super(ErrorCode.EVENT_RECEIPT_NOT_FOUND);
    }
}
