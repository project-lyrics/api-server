package com.projectlyrics.server.domain.record.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class RecordNotFoundException extends FeelinException {

    public RecordNotFoundException() {
        super(ErrorCode.RECORD_NOT_FOUND);
    }
}
