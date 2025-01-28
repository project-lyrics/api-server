package com.projectlyrics.server.global.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import java.util.Map;

public class UpdateRequiredException extends FeelinException {

    public UpdateRequiredException() {
        super(ErrorCode.UPDATE_REQUIRED, Map.of("appStoreUrl", "https://apps.apple.com/kr/app/6738319829"));
    }
}
