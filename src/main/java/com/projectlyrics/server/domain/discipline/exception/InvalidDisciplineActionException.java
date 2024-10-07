package com.projectlyrics.server.domain.discipline.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidDisciplineActionException extends FeelinException {

    public InvalidDisciplineActionException() {
        super(ErrorCode.INVALID_DISCIPLINE_ACTION);
    }
}
