package com.projectlyrics.server.domain.notification.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class UnknownNotificationException extends FeelinException {

    public UnknownNotificationException() {
        super(ErrorCode.UNKNOWN_NOTIFICATION_TYPE);
    }
}
