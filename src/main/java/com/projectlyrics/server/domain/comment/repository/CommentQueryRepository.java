package com.projectlyrics.server.domain.comment.repository;

import com.projectlyrics.server.domain.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentQueryRepository {

    Optional<Comment> findById(Long id);

    List<Comment> findAllByNoteId(Long noteId, Long userId);
}
