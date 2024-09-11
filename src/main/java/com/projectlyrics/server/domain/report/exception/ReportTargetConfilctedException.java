package com.projectlyrics.server.domain.report.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class ReportTargetConfilctedException extends FeelinException {
    public ReportTargetConfilctedException() {
        super(ErrorCode.REPORT_TARGET_CONFLICT);}
}
