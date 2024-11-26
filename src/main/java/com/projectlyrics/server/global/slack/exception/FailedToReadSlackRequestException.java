package com.projectlyrics.server.global.slack.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FailedToReadSlackRequestException extends FeelinException {

    public FailedToReadSlackRequestException() {
        super(ErrorCode.FAILED_TO_READ_SLACK_REQUEST);
    }
}
