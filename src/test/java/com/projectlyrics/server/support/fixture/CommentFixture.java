package com.projectlyrics.server.support.fixture;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.comment.domain.CommentCreate;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public class CommentFixture extends BaseFixture {

    public static Comment create(Note note, User writer) {
        return Comment.createWithId(
                getUniqueId(),
                CommentCreate.of(
                        "content",
                        writer,
                        note
                )
        );
    }
}
