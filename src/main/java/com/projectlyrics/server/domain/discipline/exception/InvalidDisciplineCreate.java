package com.projectlyrics.server.domain.discipline.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvalidDisciplineCreate extends FeelinException {

    public InvalidDisciplineCreate() {
        super(ErrorCode.INVALID_DISCIPLINE_CREATE);
    }
}

