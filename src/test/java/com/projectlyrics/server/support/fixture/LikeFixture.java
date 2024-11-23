package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.like.domain.Like;
import com.projectlyrics.server.domain.like.domain.LikeCreate;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public class LikeFixture extends BaseFixture{

    public static Like create(User user, Note note) {
        return Like.createWithId(
                getUniqueId(),
                LikeCreate.of(
                        user,
                        note
                )
        );
    }
}
