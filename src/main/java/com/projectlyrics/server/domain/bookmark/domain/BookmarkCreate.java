package com.projectlyrics.server.domain.bookmark.domain;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public record BookmarkCreate(
        User user,
        Note note
) {

    public static BookmarkCreate of(User user, Note note) {
        return new BookmarkCreate(user, note);
    }
}
