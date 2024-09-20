package com.projectlyrics.server.domain.report.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class DuplicateReportException extends FeelinException {
    public DuplicateReportException() {
        super(ErrorCode.DUPLICATE_REPORT);
    }
}
