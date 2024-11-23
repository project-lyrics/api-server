package com.projectlyrics.server.domain.block.repository;

import com.projectlyrics.server.domain.block.domain.Block;

import java.util.Optional;

public interface BlockQueryRepository {

    Optional<Block> findNotDeletedByBlockerIdAndBlockedId(long blockerId, long blockedId);
}
