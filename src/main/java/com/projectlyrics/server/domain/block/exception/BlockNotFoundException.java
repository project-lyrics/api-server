package com.projectlyrics.server.domain.block.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class BlockNotFoundException extends FeelinException {

    public BlockNotFoundException() {
        super(ErrorCode.BLOCK_NOT_FOUND);
    }
}
