package com.projectlyrics.server.domain.notification.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class NotificationReceiverUnmatchException extends FeelinException {

    public NotificationReceiverUnmatchException() {
        super(ErrorCode.NOTIFICATION_RECEIVER_UNMATCH);
    }
}
