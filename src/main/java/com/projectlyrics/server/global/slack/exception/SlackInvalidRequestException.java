package com.projectlyrics.server.global.slack.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class SlackInvalidRequestException extends FeelinException {

    public SlackInvalidRequestException() {
        super(ErrorCode.SLACK_INVALID_REQUEST);
    }
}
