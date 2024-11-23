package com.projectlyrics.server.domain.block.exception;

import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.FeelinException;

public class BlockAlreadyExistsException extends FeelinException {

    public BlockAlreadyExistsException() {
        super(ErrorCode.BLOCK_ALREADY_EXISTS);
    }
}
