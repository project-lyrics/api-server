package com.projectlyrics.server.global.slack.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class SlackInteractionFailureException extends FeelinException {
    public SlackInteractionFailureException() {
        super(ErrorCode.SLACK_INTERACTION_FAILED);
    }
}
