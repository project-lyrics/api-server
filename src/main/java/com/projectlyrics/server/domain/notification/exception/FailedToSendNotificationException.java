package com.projectlyrics.server.domain.notification.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FailedToSendNotificationException extends FeelinException {

    public FailedToSendNotificationException() {
        super(ErrorCode.NOTIFICATION_SEND_FAILED);
    }
}
