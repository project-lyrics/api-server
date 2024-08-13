package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.bookmark.domain.Bookmark;
import com.projectlyrics.server.domain.bookmark.domain.BookmarkCreate;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public class BookmarkFixture extends BaseFixture {

    public static Bookmark create(User user, Note note) {
        return Bookmark.createWithId(
                getUniqueId(),
                BookmarkCreate.of(
                        user,
                        note
                )
        );
    }
}
