package com.projectlyrics.server.domain.notification.domain.event;

import com.projectlyrics.server.domain.comment.domain.Comment;
import com.projectlyrics.server.domain.note.entity.Note;
import com.projectlyrics.server.domain.user.entity.User;

public record CommentEvent(
        User sender,
        User receiver,
        Note note,
        Comment comment
) {

    public static CommentEvent from(Comment comment) {
        return new CommentEvent(
                comment.getWriter(),
                comment.getNote().getPublisher(),
                comment.getNote(),
                comment
        );
    }
}
