package com.projectlyrics.server.domain.comment.domain;

import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public record CommentCreate(
        String content,
        User writer,
        Note note
) {

    public static CommentCreate of(String content, User writer, Note note) {
        return new CommentCreate(content, writer, note);
    }
}
