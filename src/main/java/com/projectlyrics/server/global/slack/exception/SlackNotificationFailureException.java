package com.projectlyrics.server.global.slack.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class SlackNotificationFailureException extends FeelinException {
    public SlackNotificationFailureException() {
        super(ErrorCode.SLACK_SEND_FAILED);
    }
}
