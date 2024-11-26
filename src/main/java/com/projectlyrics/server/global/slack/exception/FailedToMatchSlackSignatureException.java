package com.projectlyrics.server.global.slack.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class FailedToMatchSlackSignatureException extends FeelinException {

    public FailedToMatchSlackSignatureException() {
        super(ErrorCode.FALIED_TO_MATCH_SLACK_SIGNATURE_EXCEPTION);
    }
}
