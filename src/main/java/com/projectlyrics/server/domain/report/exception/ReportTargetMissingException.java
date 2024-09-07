package com.projectlyrics.server.domain.report.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class ReportTargetMissingException extends FeelinException {

    public ReportTargetMissingException() {
        super(ErrorCode.REPORT_TARGET_MISSING);
    }
}
