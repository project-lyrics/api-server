package com.projectlyrics.server.global.slack.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class SlackNotificationProcessingException extends FeelinException {
    public SlackNotificationProcessingException() {
        super(ErrorCode.SLACK_PROCESSING_ERROR);
    }
}
