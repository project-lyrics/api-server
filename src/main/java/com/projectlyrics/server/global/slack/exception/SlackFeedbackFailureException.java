package com.projectlyrics.server.global.slack.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class SlackFeedbackFailureException extends FeelinException {
    public SlackFeedbackFailureException() {
        super(ErrorCode.SLACK_FEEDBACK_FAILED);
    }
}
