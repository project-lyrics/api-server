package com.projectlyrics.server.domain.discipline.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class InvaildDisciplineAction extends FeelinException {

    public InvaildDisciplineAction() {
        super(ErrorCode.INVAILD_DISCIPLINE_ACTION);
    }
}
