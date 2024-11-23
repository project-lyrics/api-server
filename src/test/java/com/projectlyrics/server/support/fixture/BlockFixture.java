package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.block.domain.Block;
import com.projectlyrics.server.domain.user.entity.User;

public class BlockFixture extends BaseFixture{
    public static Block create(User blocker, User blocked) {
        return Block.createWithId(
                getUniqueId(),
                blocker,
                blocked
        );
    }
}
