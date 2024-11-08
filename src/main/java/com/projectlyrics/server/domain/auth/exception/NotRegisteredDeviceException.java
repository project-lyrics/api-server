package com.projectlyrics.server.domain.auth.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class NotRegisteredDeviceException extends FeelinException {

    public NotRegisteredDeviceException() {
        super(ErrorCode.NOT_REGISTERED_DEVICE);
    }
}
