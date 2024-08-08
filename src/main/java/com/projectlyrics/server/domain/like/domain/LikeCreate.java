package com.projectlyrics.server.domain.like.domain;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public record LikeCreate(
        User user,
        Note note
) {

    public static LikeCreate of(User user, Note note) {
        return new LikeCreate(user, note);
    }
}
